package com.atguigu.redisson;

import org.junit.jupiter.api.Test;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * projectName: shop_parent
 *
 * @author: WangYiBing
 * time: 02.05 005 下午 20:55 周日
 * description:
 */
@SpringBootTest
public class RedissonTest {
    @Autowired
    private RedissonClient redissonClient;

    @Test
    public void testRedisson(){
        System.out.println(redissonClient);
    }
}
