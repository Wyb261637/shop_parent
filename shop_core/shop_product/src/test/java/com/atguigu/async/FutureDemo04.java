package com.atguigu.async;

import com.atguigu.exception.SleepUtils;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * projectName: shop_parent
 *
 * @author: WangYiBing
 * time: 2023/9/2 18:29 周六
 * description:
 */
public class FutureDemo04 {
    public static void main(String[] args) {
        supplyAsync();
        SleepUtils.second(5);
        System.out.println("main线程执行……");
    }

    //发起异步请求
    public static void supplyAsync() {
      CompletableFuture<String> supply  = CompletableFuture.supplyAsync(new Supplier<String>() {
            @Override
            public String get() {
                System.out.println(Thread.currentThread().getName() + "你好supplyAsync");
                SleepUtils.second(2);
                return "Respect";
            }
        });
        supply.thenAccept(new Consumer<String>() {
            @Override
            public void accept(String acceptVal) {
                System.out.println(Thread.currentThread().getName() + "第一个thenAccept接收的值"+acceptVal);
            }
        });
        supply.thenAccept(new Consumer<String>() {
            @Override
            public void accept(String acceptVal) {
                System.out.println(Thread.currentThread().getName() + "第二个thenAccept接收的值"+acceptVal);
            }
        });
    }
}