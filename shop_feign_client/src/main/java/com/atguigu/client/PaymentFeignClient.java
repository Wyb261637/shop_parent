package com.atguigu.client;

import com.atguigu.entity.PaymentInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * projectName: shop_parent
 *
 * @author: WangYiBing
 * time: 2024/1/22 22:16 周一
 * description:
 */
@FeignClient(value = "shop-payment")
public interface PaymentFeignClient {
    /**
     * 1.退款
     *
     * @param orderId
     * @return
     */
    @GetMapping("/payment/refund/{orderId}")
    boolean refund(@PathVariable Long orderId);

    /**
     * 2.查询支付宝是否有交易记录
     *
     * @param orderId
     * @return
     */
    @GetMapping("/payment/queryAlipayTrade/{orderId}")
    boolean queryAlipayTrade(@PathVariable Long orderId);

    /**
     * 3.交易关闭
     *
     * @param orderId
     * @return
     */
    @GetMapping("/payment/closeAlipayTrade/{orderId}")
    boolean closeAlipayTrade(@PathVariable Long orderId);

    /**
     * 4.根据outTradeNo查询支付表单信息
     *
     * @param outTradeNo
     * @return
     */
    @GetMapping("/payment/getPaymentInfo/{outTradeNo}")
    PaymentInfo getPaymentInfo(@PathVariable String outTradeNo);
}
