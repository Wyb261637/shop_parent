package com.atguigu.service.impl;

import com.alibaba.fastjson.JSON;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.response.AlipayTradePagePayResponse;
import com.atguigu.client.OrderFeignClient;
import com.atguigu.config.AlipayConfig;
import com.atguigu.constant.MqConst;
import com.atguigu.entity.OrderInfo;
import com.atguigu.entity.PaymentInfo;
import com.atguigu.enums.PaymentStatus;
import com.atguigu.enums.PaymentType;
import com.atguigu.mapper.PaymentInfoMapper;
import com.atguigu.service.PaymentInfoService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jetbrains.annotations.NotNull;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * 支付信息表 服务实现类
 * </p>
 *
 * @author WangYiBing
 * @since 2024-01-06
 */
@Service
public class PaymentInfoServiceImpl extends ServiceImpl<PaymentInfoMapper, PaymentInfo> implements PaymentInfoService {

    @Autowired
    private AlipayClient alipayClient;
    @Autowired
    private OrderFeignClient orderFeignClient;
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Override
    public String createQrCode(Long orderId) throws AlipayApiException {
        //根据订单id查询订单信息
        OrderInfo orderInfo = orderFeignClient.getOrderInfoById(orderId);
        //保存支付订单信息表
        savePaymentInfo(orderInfo);
        //调用支付宝接口生成二维码
        AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
        //支付成功之后的异步通知，支付宝调用我们的商户系统
        request.setNotifyUrl(AlipayConfig.notify_payment_url);
        ////支付成功之后的同步通知，支付成功之后跳转到商户指定页面
        request.setReturnUrl(AlipayConfig.return_payment_url);
        //构造json参数
//        JSONObject bizContent = new JSONObject();
        Map<String, Object> bizMap = new HashMap<>();
        if (orderInfo != null) {
            //商户订单号，商家自定义，保持唯一性
//            bizContent.put("out_trade_no", orderInfo.getOutTradeNo());
            //支付总金额，最小值0.01元
//            bizContent.put("total_amount", orderInfo.getTotalMoney());
            //订单标题，不可使用特殊符号
//            bizContent.put("subject", "天气太热，买个锤子手机！");
            //电脑网站支付场景固定传值FAST_INSTANT_TRADE_PAY
//            bizContent.put("product_code", "FAST_INSTANT_TRADE_PAY");
            bizMap.put("out_trade_no", orderInfo.getOutTradeNo());
            bizMap.put("total_amount", orderInfo.getTotalMoney());
            bizMap.put("subject", "天气太热，买个锤子手机！");
            bizMap.put("product_code", "FAST_INSTANT_TRADE_PAY");
        }
//        request.setBizContent(bizContent.toString());
        request.setBizContent(JSON.toJSONString(bizMap));
        AlipayTradePagePayResponse response = alipayClient.pageExecute(request);
        if (response.isSuccess()) {
            //返回支付宝调用成功页面
            return response.getBody();
        } else {
            System.out.println("调用失败");
        }
        return null;
    }

    @Override
    public PaymentInfo getPaymentInfo(String outTradeNo) {
        QueryWrapper<PaymentInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("out_trade_no", outTradeNo);
        wrapper.eq("payment_type", PaymentType.ALIPAY.name());
        return baseMapper.selectOne(wrapper);
    }

    @Override
    public void updatePaymentInfo(Map<String, String> aliPayParam) {
        //根据商户订单号，更新支付信息
        String outTradeNo = aliPayParam.get("out_trade_no");
        PaymentInfo paymentInfo = getPaymentInfo(outTradeNo);
        //修改支付信息为已支付
        paymentInfo.setPaymentStatus(aliPayParam.get("trade_status"));
        //保存支付宝给的交易号
        paymentInfo.setTradeNo(aliPayParam.get("trade_no"));
        //保存整个支付传递的参数信息
        paymentInfo.setCallbackTime(new Date());
        paymentInfo.setCallbackContent(aliPayParam.toString());
        //更新支付信息
        baseMapper.updateById(paymentInfo);
        //发送消息到RabbitMQ，通知订单服务，更新订单状态
        rabbitTemplate.convertAndSend(MqConst.PAY_ORDER_EXCHANGE,
                MqConst.PAY_ORDER_ROUTE_KEY, paymentInfo.getOrderId());
    }

    private void savePaymentInfo(@NotNull OrderInfo order) {
        //判断支付表单里面是否有添加过该记录
        QueryWrapper<PaymentInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("order_id", order.getId());
        wrapper.eq("payment_type", PaymentType.ALIPAY.name());
        Integer count = baseMapper.selectCount(wrapper);
        if (count > 0) {
            return;
        }
        PaymentInfo paymentInfo = new PaymentInfo();
        paymentInfo.setOutTradeNo(order.getOutTradeNo());
        paymentInfo.setOrderId(String.valueOf(order.getId()));
        paymentInfo.setPaymentType(PaymentType.ALIPAY.name());
        paymentInfo.setPaymentMoney(order.getTotalMoney());
        paymentInfo.setPaymentContent(order.getTradeBody());
        paymentInfo.setPaymentStatus(PaymentStatus.UNPAID.name());
        paymentInfo.setCreateTime(new Date());
        baseMapper.insert(paymentInfo);
    }
}
