package com.atguigu.service;

import com.alipay.api.AlipayApiException;
import com.atguigu.entity.PaymentInfo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
 * <p>
 * 支付信息表 服务类
 * </p>
 *
 * @author WangYiBing
 * @since 2024-01-06
 */
public interface PaymentInfoService extends IService<PaymentInfo> {
    /**
     * 1.创建支付二维码
     *
     * @param orderId
     * @return
     */
    String createQrCode(Long orderId) throws AlipayApiException;

    /**
     * 2.查询支付表信息
     * @param outTradeNo
     * @return
     */
    PaymentInfo getPaymentInfo(String outTradeNo);

    /**
     * 3.修改支付表信息
     * @param aliPayParam
     */
    void updatePaymentInfo(Map<String, String> aliPayParam);

    /**
     * 4.退款
     * @param orderId
     * @return
     */
    boolean refund(Long orderId) throws AlipayApiException;

    /**
     * 5.查询支付宝是否有交易记录
     * @param orderId
     * @return
     */
    boolean queryAlipayTrade(Long orderId) throws AlipayApiException;

    /**
     * 6.交易关闭
     * @param orderId
     * @return
     */
    boolean closeAlipayTrade(Long orderId) throws AlipayApiException;
}
