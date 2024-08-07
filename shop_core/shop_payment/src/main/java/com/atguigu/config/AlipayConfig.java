package com.atguigu.config;

import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * projectName: shop_parent
 *
 * @author: WangYiBing
 * time: 2024/1/6 14:21 周六
 * description:
 */
@Configuration
public class AlipayConfig {
    //获取配置文件中的数据
    @Value("${alipay_url}")
    private String alipay_url;
    @Value("${app_private_key}")
    private String app_private_key;
    @Value("${app_id}")
    private String app_id;

    public final static String format = "json";
    public final static String charset = "utf-8";
    public final static String sign_type = "RSA2";

    public static String return_payment_url;
    public static String notify_payment_url;
    public static String alipay_public_key;

    @Value("${alipay_public_key}")
    public void setAlipay_public_key(String alipay_public_key) {
        AlipayConfig.alipay_public_key = alipay_public_key;
    }

    @Value("${return_payment_url}")
    public void setReturn_url(String return_payment_url) {
        AlipayConfig.return_payment_url = return_payment_url;
    }

    @Value("${notify_payment_url}")
    public void setNotify_url(String notify_payment_url) {
        AlipayConfig.notify_payment_url = notify_payment_url;
    }

    @Bean
    public AlipayClient alipayClient() {
        AlipayClient alipayClient = new DefaultAlipayClient(alipay_url, app_id, app_private_key, format, charset, alipay_public_key, sign_type);
        return alipayClient;
    }
}
