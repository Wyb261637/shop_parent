package com.atguigu.service.impl;

import com.atguigu.client.ProductFeignClient;
import com.atguigu.constant.MqConst;
import com.atguigu.entity.OrderDetail;
import com.atguigu.entity.OrderInfo;
import com.atguigu.enums.ProcessStatus;
import com.atguigu.mapper.OrderInfoMapper;
import com.atguigu.service.OrderDetailService;
import com.atguigu.service.OrderInfoService;
import com.atguigu.util.HttpClientUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

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
    public Long saveOrderAndDetail(OrderInfo orderInfo) {
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


}
