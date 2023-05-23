package com.atguigu.controller;


import com.atguigu.exception.SleepUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * projectName: shop_parent
 *
 * @author: WangYiBing
 * time: 02.05 005 下午 21:07 周日
 * description:
 */
@RestController
@RequestMapping("/product")
public class RedissonController {

    @Autowired
    private RedissonClient redissonClient;

    @GetMapping("lock1")
    public String lock1(){
        RLock lock = redissonClient.getLock("lock");
        //加锁 一直等到拿锁成功 默认加锁方式
        String uuid=null;
        lock.lock();
        try {
            uuid = UUID.randomUUID().toString();
            SleepUtils.second(2);
            System.out.println(Thread.currentThread().getName() + "执行业务"+uuid);
        } finally {
            lock.unlock();
        }
        return Thread.currentThread().getName() + "执行业务"+uuid;
    }
}
