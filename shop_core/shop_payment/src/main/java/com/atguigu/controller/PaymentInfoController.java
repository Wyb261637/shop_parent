package com.atguigu.controller;


import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.atguigu.config.AlipayConfig;
import com.atguigu.entity.PaymentInfo;
import com.atguigu.enums.PaymentStatus;
import com.atguigu.service.PaymentInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * <p>
 * 支付信息表 前端控制器
 * </p>
 *
 * @author WangYiBing
 * @since 2024-01-06
 */
@RestController
@RequestMapping("/payment")
public class PaymentInfoController {
    @Autowired
    private PaymentInfoService paymentInfoService;

    /**
     * 1.创建支付二维码
     *
     * @param orderId
     * @return
     */
    @RequestMapping("createQrCode/{orderId}")
    public String createQrCode(@PathVariable Long orderId) throws AlipayApiException {
        return paymentInfoService.createQrCode(orderId);
    }

    /**
     * 2.支付成功后支付宝调用我们的地址
     *
     * @param aliPayParam
     * @return
     */
    @PostMapping("async/notify")
    public String asyncNotify(@RequestBody Map<String, String> aliPayParam) throws AlipayApiException {
        //调用SDK验证签名
        boolean signVerified = AlipaySignature.rsaCheckV1(aliPayParam,
                AlipayConfig.alipay_public_key, AlipayConfig.charset, AlipayConfig.sign_type);
        if (signVerified) {
            // 验签成功后，按照支付结果异步通知中的描述，对支付结果中的业务内容进行二次校验，
            String tradeStatus = aliPayParam.get("trade_status");
            if ("TRADE_SUCCESS".equals(tradeStatus) || "TRADE_FINISHED".equals(tradeStatus)) {
                //查询支付表信息
                String outTradeNo = aliPayParam.get("out_trade_no");
                PaymentInfo paymentInfo = paymentInfoService.getPaymentInfo(outTradeNo);
                //获取支付表状态
                String paymentStatus = paymentInfo.getPaymentStatus();
                //如果支付表信息已经支付或者已关闭，就不需要再执行了
                if (paymentStatus.equals(PaymentStatus.PAID.name()) || paymentStatus.equals(PaymentStatus.ClOSED.name())) {
                    return "success";
                }
                //修改支付表信息
                paymentInfoService.updatePaymentInfo(aliPayParam);
            }
        } else {
            //验签失败则记录异常日志，并在response中返回failure.
            return "failure";
        }
        return "failure";
    }
}

