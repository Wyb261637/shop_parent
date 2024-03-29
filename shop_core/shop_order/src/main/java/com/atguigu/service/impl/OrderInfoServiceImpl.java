package com.atguigu.service.impl;

import com.alibaba.fastjson.JSON;
import com.atguigu.client.ProductFeignClient;
import com.atguigu.constant.MqConst;
import com.atguigu.entity.OrderDetail;
import com.atguigu.entity.OrderInfo;
import com.atguigu.enums.ProcessStatus;
import com.atguigu.mapper.OrderInfoMapper;
import com.atguigu.service.OrderDetailService;
import com.atguigu.service.OrderInfoService;
import com.atguigu.util.HttpClientUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.*;

/**
 * <p>
 * 订单表 订单表 服务实现类
 * </p>
 *
 * @author WangYiBing
 * @since 2023-12-10
 */
@Service
public class OrderInfoServiceImpl extends ServiceImpl<OrderInfoMapper, OrderInfo> implements OrderInfoService {

    @Autowired
    private OrderDetailService orderDetailService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private ProductFeignClient productFeignClient;
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Value("${cancel.order.delay}")
    private Integer cancelOrderDelay;

    @Override
    public Long saveOrderAndDetail(OrderInfo orderInfo,String userId) {
        //商品对外交易号
        String outTradeNo = "atguigu" + System.currentTimeMillis();
        orderInfo.setOutTradeNo(outTradeNo);
        orderInfo.setTradeBody("手机很不错，值得回购！");
        orderInfo.setCreateTime(new Date());
        //订单过期时间
        Calendar instance = Calendar.getInstance();
        instance.add(Calendar.MINUTE, 15);
        orderInfo.setExpireTime(instance.getTime());
        //订单的进度状态
        orderInfo.setProcessStatus(ProcessStatus.UNPAID.name());
        orderInfo.setUserId(Long.parseLong(userId));
        //保存订单的基本信息
        baseMapper.insert(orderInfo);
        //保存订单的详情信息
        Long orderId = orderInfo.getId();
        List<OrderDetail> orderDetailList = orderInfo.getOrderDetailList();
        if (!CollectionUtils.isEmpty(orderDetailList)) {
            for (OrderDetail orderDetail : orderDetailList) {
                orderDetail.setOrderId(orderId);
            }
        }
        orderDetailService.saveBatch(orderDetailList);
        //发送一个延时消息，超时自动取消订单
        rabbitTemplate.convertAndSend(MqConst.CANCEL_ORDER_EXCHANGE,
                MqConst.CANCEL_ORDER_ROUTE_KEY,
                orderId,
                correlationData -> {
                    correlationData.getMessageProperties().setDelay(cancelOrderDelay);
                    return correlationData;
                });
        return orderId;
    }

    @Override
    public String generateTradeNo(String userId) {
        String tradeNo = UUID.randomUUID().toString();
        //往redis中存一份
        String tradeNoKey = "user" + userId + ":tradeNo";
        redisTemplate.opsForValue().set(tradeNoKey, tradeNo);
        return tradeNo;
    }

    @Override
    public boolean compareTradeNo(String userId, String tradeNoUI) {
        String tradeNoKey = "user" + userId + ":tradeNo";
        String redisTradeNo = (String) redisTemplate.opsForValue().get(tradeNoKey);
        return tradeNoUI.equals(redisTradeNo);
    }

    @Override
    public void deleteTradeNo(String userId) {
        String tradeNoKey = "user" + userId + ":tradeNo";
        redisTemplate.delete(tradeNoKey);
    }

    @Override
    public String checkStockAndPrice(String userId, OrderInfo orderInfo) {
        //a.拿到用户的购买的商品清单
        StringBuilder sb = new StringBuilder();
        List<OrderDetail> orderDetailList = orderInfo.getOrderDetailList();
        if (!CollectionUtils.isEmpty(orderDetailList)) {
            for (OrderDetail orderDetail : orderDetailList) {
                Long skuId = orderDetail.getSkuId();
                String skuNum = orderDetail.getSkuNum();
                //b.判断每个商品库存是否足够 访问库存接口
                String url = "http://localhost:8100/hasStock?skuId=" + skuId + "&num=" + skuNum;
                //发起请求
                String result = HttpClientUtil.doGet(url);
                //0无库存 1有库存
                if ("0".equals(result)) {
                    sb.append(orderDetail.getSkuName() + "库存不足！");
                }
                //判断价格
                BigDecimal realTimePrice = productFeignClient.getSkuPrice(skuId);
                BigDecimal orderPrice = orderDetail.getOrderPrice();
                //如果价格不相等
                if (realTimePrice.compareTo(orderPrice) != 0) {
                    sb.append(orderDetail.getSkuName() + "价格有变化,请刷新页面！");
                }
            }
        }
        return sb.toString();
    }

    @Override
    public OrderInfo getOrderInfoAndDetail(Long orderId) {
        //1.查询订单基本信息
        OrderInfo orderInfo = baseMapper.selectById(orderId);
        if (orderInfo != null) {
            //2.查询订单详细信息
            QueryWrapper<OrderDetail> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("order_id", orderId);
            List<OrderDetail> orderDetailList = orderDetailService.list(queryWrapper);
            orderInfo.setOrderDetailList(orderDetailList);
        }
        return orderInfo;
    }

    @Override
    public void updateOrderStatus(OrderInfo orderInfo, ProcessStatus status) {
        orderInfo.setOrderStatus(status.getOrderStatus().name());
        orderInfo.setProcessStatus(status.name());
        baseMapper.updateById(orderInfo);
    }

    @Override
    public void sendMsgToWareHouse(OrderInfo orderInfo) {
        //a.将订单状态改为已通知仓库
        updateOrderStatus(orderInfo, ProcessStatus.NOTIFIED_WARE);
        //b.需要组织一个json类型的数据给仓库系统
        String jsonData = JSON.toJSONString(assembleWareHouseData(orderInfo));
        //c.发消息给仓库系统
        rabbitTemplate.convertAndSend(MqConst.DECREASE_STOCK_EXCHANGE, MqConst.DECREASE_STOCK_ROUTE_KEY, jsonData);

    }

    @Override
    public String splitOrder(Long orderId, String wareHouseIdSkuIdMapJson) {
        //1.获取原始订单信息
        OrderInfo parentOrderInfo = getOrderInfoAndDetail(orderId);
        //参数格式转换为list [{"wareHouseId":"1","skuIdList":["24","28"]},{"wareHouseId":"2","skuIdList":["25,30,32"]}]
        List<Map> wareHouseIdSkuIdMapList = JSON.parseArray(wareHouseIdSkuIdMapJson, Map.class);
        //构造多个订单拼接好的json数据
        List<Object> assembleWareHouseDataList=new ArrayList<>();
        for (Map wareHouseIdSkuIdMap : wareHouseIdSkuIdMapList) {
            String wareHouseId = (String) wareHouseIdSkuIdMap.get("wareHouseId");
            List<String> skuIdList = (List<String>) wareHouseIdSkuIdMap.get("skuIdList");
            //2.设置子订单信息
            OrderInfo childOrderInfo = new OrderInfo();
            BeanUtils.copyProperties(parentOrderInfo, childOrderInfo);
            childOrderInfo.setParentOrderId(orderId);
            childOrderInfo.setId(null);
            //设置仓库Id
            childOrderInfo.setWareHouseId(wareHouseId);
            //3.设置子订单详细信息 该子订单有哪些明细 子订单金额
            BigDecimal childTotalMoney = new BigDecimal("0");
            List<OrderDetail> childOrderDetailList = new ArrayList<>();
            List<OrderDetail> parentOrderDetailList = parentOrderInfo.getOrderDetailList();
            for (OrderDetail parentOrderDetail : parentOrderDetailList) {
                for (String skuId : skuIdList) {
                    if (Long.parseLong(skuId) == parentOrderDetail.getSkuId()) {
                        BigDecimal orderPrice = parentOrderDetail.getOrderPrice();
                        String skuNum = parentOrderDetail.getSkuNum();
                        childTotalMoney = childTotalMoney.add(orderPrice.multiply(new BigDecimal(skuNum)));
                        childOrderDetailList.add(parentOrderDetail);
                    }
                }
            }
            childOrderInfo.setTotalMoney(childTotalMoney);
            childOrderInfo.setOrderDetailList(childOrderDetailList);
            //保存子订单及其订单详细信息
            saveOrderAndDetail(childOrderInfo,null);
            //构造多个子订单信息
            Map<String, Object> warehouseMap = assembleWareHouseData(childOrderInfo);
            assembleWareHouseDataList.add(warehouseMap);
        }
        //4.把原始订单改为已拆分
        updateOrderStatus(parentOrderInfo, ProcessStatus.SPLIT);
        //5.返回信息给仓库系统
        return JSON.toJSONString(assembleWareHouseDataList);
    }

    /**
     * 拼接的是一个订单信息
     * @param orderInfo
     * @return
     */
    private Map<String, Object> assembleWareHouseData(OrderInfo orderInfo) {
        //构造一个map结构用于封装数据
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("orderId", orderInfo.getId());
        dataMap.put("consignee", orderInfo.getConsignee());
        dataMap.put("consigneeTel", orderInfo.getConsigneeTel());
        dataMap.put("orderComment", orderInfo.getOrderComment());
        dataMap.put("orderBody", orderInfo.getTradeBody());
        dataMap.put("deliveryAddress", orderInfo.getDeliveryAddress());
        dataMap.put("paymentWay", 2);
        //设置一个仓库Id 给仓库拆完单之后返回值用的
        if (!StringUtils.isEmpty(orderInfo.getWareHouseId())) {
            dataMap.put("wareId", orderInfo.getWareHouseId());
        }
        //构造商品清单
        List<Map<String, Object>> orderDetailMapList = new ArrayList<>();
        List<OrderDetail> orderDetailList = orderInfo.getOrderDetailList();
        for (OrderDetail orderDetail : orderDetailList) {
            Map<String, Object> orderDetailMap = new HashMap<>();
            orderDetailMap.put("skuId", orderDetail.getSkuId());
            orderDetailMap.put("skuNum", orderDetail.getSkuNum());
            orderDetailMap.put("skuName", orderDetail.getSkuName());
            orderDetailMapList.add(orderDetailMap);
        }
        dataMap.put("details", orderDetailMapList);
        return dataMap;
    }
}
