package com.atguigu.async;

import com.atguigu.exception.SleepUtils;

import java.util.concurrent.CompletableFuture;

/**
 * projectName: shop_parent
 *
 * @author: WangYiBing
 * time: 2023/9/2 18:29 周六
 * description:
 */
public class FutureDemo07 {
    public static void main(String[] args) throws Exception {
        supplyAsync();
        SleepUtils.second(5);
        System.out.println("main线程执行……");
    }

    //发起异步请求
    public static void supplyAsync() throws Exception {
        CompletableFuture<String> supply = CompletableFuture.supplyAsync(() -> {
            System.out.println(Thread.currentThread().getName() + "你好supplyAsync");
            SleepUtils.second(6);
            return "Respect";
        });
        CompletableFuture<String> thenApply1 = supply.thenApplyAsync((String acceptVal)->{
            System.out.println(Thread.currentThread().getName() + "第一个thenApplyAsync接收的值" + acceptVal);
            return "thenApply1" + acceptVal;
        });
        CompletableFuture<String> thenApply2 = supply.thenApplyAsync((String acceptVal)->{
            System.out.println(Thread.currentThread().getName() + "第二个thenApplyAsync接收的值" + acceptVal);
            return "thenApply2" + acceptVal;
        });
        System.out.println(thenApply1.get());
        System.out.println(thenApply2.get());
    }
}