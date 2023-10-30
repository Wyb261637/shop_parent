package com.atguigu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
/**
 * projectName: shop_parent
 *
 * @author: WangYiBing
 * time: 2023/9/4 23:59 周一
 * description:
 */
@EnableDiscoveryClient
@SpringBootApplication
public class UserApplication {
   public static void main(String[] args) {
      SpringApplication.run(UserApplication.class, args);
   }
}