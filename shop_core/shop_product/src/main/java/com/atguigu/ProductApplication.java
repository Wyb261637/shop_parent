package com.atguigu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * projectName: shop_parent
 *
 * @author: WangYiBing
 * time: 2022/12/27 10:54 周二
 * description: gateway网关启动类
 */
@EnableDiscoveryClient
@SpringBootApplication
@EnableFeignClients
@EnableSwagger2
//强制扫描
//@Import(MybatisPlusConfig.class)
public class ProductApplication {
    public static void main(String[] args) {
        System.out.println("23323");
        SpringApplication.run(ProductApplication.class, args);
    }
}