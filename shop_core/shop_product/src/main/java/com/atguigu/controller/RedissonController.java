package com.atguigu.controller;


import com.atguigu.exception.SleepUtils;
import org.redisson.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

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

    /**
     * 1.可重入锁（最基本的锁）
     * a.默认有30s过期时间
     * b.每隔10s自动续期
     * c.看门狗机制
     * Redisson实例被关闭前，不断延长锁的有效期
     *
     * @return
     */
    @GetMapping("lock1")
    public String lock1() {
        RLock lock = redissonClient.getLock("lock");
        //加锁 一直等到拿锁成功 默认加锁方式
        lock.lock();
        try {
            SleepUtils.second(2);
            System.out.println(Thread.currentThread().getName() + "执行业务" + UUID.randomUUID());
        } finally {
            lock.unlock();
        }
        return Thread.currentThread().getName() + "执行业务" + UUID.randomUUID();
    }

    /**
     * 2.指定锁过期时间
     *
     * @return
     */
    @GetMapping("lock2")
    public String lock2() {
        RLock lock = redissonClient.getLock("lock");
        //加锁 10s后自动解锁，不会有自动续期，无需调用unlock方法自动解锁，不怎么采用该方式，会出现已经自动解锁然后重复解锁的情况
        lock.lock(10, TimeUnit.SECONDS);
        try {
            SleepUtils.second(30);
            System.out.println(Thread.currentThread().getName() + "执行业务" + UUID.randomUUID());
        } finally {
            //无需自动解锁
            lock.unlock();
        }
        return Thread.currentThread().getName() + "执行业务" + UUID.randomUUID();
    }

    /**
     * 3.尝试加锁
     *
     * @return
     */
    @GetMapping("lock3")
    public String lock3() {
        RLock lock = redissonClient.getLock("lock");
        //尝试加锁，最大持有锁的时间是20s，释放锁的时间是15s
        try {
            lock.tryLock(20, 5, TimeUnit.SECONDS);
            SleepUtils.second(30);
            System.out.println(Thread.currentThread().getName() + "执行业务" + UUID.randomUUID());
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }
        return Thread.currentThread().getName() + "执行业务" + UUID.randomUUID();
    }

    /**
     * 4.读写锁:测试写锁
     *
     * @return
     */
    @GetMapping("write")
    public String write() {
        RReadWriteLock rwlock = redissonClient.getReadWriteLock("rwlock");
        Lock writeLock = rwlock.writeLock();
        //尝试加锁，最大持有锁的时间是20s，释放锁的时间是15s
        writeLock.lock();
        try {
            SleepUtils.second(10);
            System.out.println(Thread.currentThread().getName() + "执行业务" + UUID.randomUUID());
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            writeLock.unlock();
        }
        return Thread.currentThread().getName() + "执行业务" + UUID.randomUUID();
    }
    /**
     * 5.读写锁:测试读锁
     *
     * @return
     */
    @GetMapping("read")
    public String read() {
        RReadWriteLock rwlock = redissonClient.getReadWriteLock("rwlock");
        Lock readLock = rwlock.readLock();
        //尝试读锁
        readLock.lock();
        try {
            System.out.println(Thread.currentThread().getName() + "执行业务" + UUID.randomUUID());
        } finally {
            readLock.unlock();
        }
        return Thread.currentThread().getName() + "执行业务" + UUID.randomUUID();
    }
    /**
     * 6.测试信号量（semaphore）
     * 停车场5个车位
     * a.占用车位
     *
     *
     * @return
     */
    @GetMapping("park")
    public String park() throws Exception {
        RSemaphore parkStation = redissonClient.getSemaphore("park_station");
        //信号量减1
        parkStation.acquire(1);
        System.out.println(Thread.currentThread().getName() + "找到车位");
        return Thread.currentThread().getName() + "找到车位";
    }/**
     * 6.测试信号量（semaphore）
     * 停车场5个车位
     *
     * b.开走、释放车位
     *
     * @return
     */
    @GetMapping("left")
    public String left() {
        RSemaphore parkStation = redissonClient.getSemaphore("park_station");
        //信号量加1
        parkStation.release(1);
        System.out.println(Thread.currentThread().getName() + "离开车位");
        return Thread.currentThread().getName() + "离开车位";
    }

    /**
     * 7.闭锁
     * @return
     */
    @GetMapping("leftClassRoom")
    public String leftClassRoom() {
        RCountDownLatch leftClass = redissonClient.getCountDownLatch("left_class");
        //如果有人走了 数量减1
        leftClass.countDown();
        System.out.println(Thread.currentThread().getName() + "学员离开");
        return Thread.currentThread().getName() + "学员离开";
    }
    /**
     * 7.闭锁
     * @return
     */
    @GetMapping("lockDoor")
    public String lockDoor() throws Exception {
        RCountDownLatch leftClass = redissonClient.getCountDownLatch("left_class");
        //所有人走了，班长才能离开
        leftClass.trySetCount(6);
        leftClass.await();
        System.out.println(Thread.currentThread().getName() + "班长离开");
        return Thread.currentThread().getName() + "班长离开";
    }
}
