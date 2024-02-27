package com.atguigu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * projectName: shop_parent
 *
 * @author: WangYiBing
 * time: 2024/1/30 21:48 周二
 * description:
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class SeckillApplication {
    public static void main(String[] args) {
        SpringApplication.run(SeckillApplication.class, args);
    }

}
