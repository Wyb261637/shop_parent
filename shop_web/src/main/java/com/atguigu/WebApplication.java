package com.atguigu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * projectName: shop_parent
 *
 * @author: WangYiBing
 * time: 2023/1/13 11:04 周五
 * description: web页面启动类
 */
@SpringBootApplication
@EnableFeignClients
@EnableDiscoveryClient//让注册中心可以扫描到该服务
public class WebApplication {
    public static void main(String[] args) {
        SpringApplication.run(WebApplication.class, args);
    }
}
