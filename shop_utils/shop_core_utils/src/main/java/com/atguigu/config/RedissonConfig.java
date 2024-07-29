package com.atguigu.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * projectName: shop_parent
 *
 * @author: WangYiBing
 * time: 02.05 005 下午 20:51 周日
 * description:
 */
@Configuration
public class RedissonConfig {

    @Bean
    public RedissonClient redissonClient(){
        Config config = new Config();
        //可以用"rediss://"来启用SSL连接
        config.useSingleServer().setAddress("redis://192.168.2.129:6379");
        //密码
        config.useSingleServer().setPassword("123456");
        return Redisson.create(config);
    }
}
