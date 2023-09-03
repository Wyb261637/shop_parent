package com.atguigu.async;

import com.atguigu.exception.SleepUtils;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

/**
 * projectName: shop_parent
 *
 * @author: WangYiBing
 * time: 2023/9/2 18:29 周六
 * description:
 */
public class FutureDemo03 {
    public static void main(String[] args) {
        runAsync2();
        SleepUtils.second(2);
        System.out.println("main线程执行……");
    }
    //发起异步请求
    public static void runAsync1() {
        CompletableFuture.runAsync(new Runnable() {
            @Override
            public void run() {
                System.out.println(Thread.currentThread().getName() + "你好runAsync");
            }
        }).whenComplete(new BiConsumer<Void, Throwable>() {
            @Override
            public void accept(Void acceptValue, Throwable throwable) {
                System.out.println(Thread.currentThread().getName() + "异步方法执行之后获取的值1" + acceptValue);
            }
        });
    }
    public static void runAsync2() {
        CompletableFuture.runAsync(new Runnable() {
            @Override
            public void run() {
                System.out.println(Thread.currentThread().getName() + "你好runAsync");
            }
        }).whenCompleteAsync(new BiConsumer<Void, Throwable>() {
            @Override
            public void accept(Void acceptValue, Throwable throwable) {
                System.out.println(Thread.currentThread().getName() + "异步方法执行之后获取的值2" + acceptValue);
            }
        });
    }
}