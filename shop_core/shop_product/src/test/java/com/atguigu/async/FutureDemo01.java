package com.atguigu.async;

import com.atguigu.exception.SleepUtils;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Supplier;

/**
 * projectName: shop_parent
 *
 * @author: WangYiBing
 * time: 2023/9/2 18:29 周六
 * description:
 */
public class FutureDemo01 {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        supplyAsync();
        System.out.println("main线程执行……");
    }

    //发起异步请求
    public static void runAsync() {
        CompletableFuture.runAsync(new Runnable() {
            @Override
            public void run() {
                System.out.println(Thread.currentThread().getName() + "你好runAsync");
                SleepUtils.second(12);
            }
        });
    }

    //发起异步请求
    public static void supplyAsync() throws ExecutionException, InterruptedException {
        CompletableFuture<String> stringCompletableFuture = CompletableFuture.supplyAsync(new Supplier<String>() {
            @Override
            public String get() {
                System.out.println(Thread.currentThread().getName() + "你好supplyAsync");
                SleepUtils.second(2);
                return "Respect";
            }
        });
        System.out.println(stringCompletableFuture.get());
    }
}