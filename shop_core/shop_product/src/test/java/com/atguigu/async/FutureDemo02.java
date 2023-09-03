package com.atguigu.async;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * projectName: shop_parent
 *
 * @author: WangYiBing
 * time: 2023/9/2 18:29 周六
 * description:
 */
public class FutureDemo02 {
    public static void main(String[] args) {
        runAsync();
        System.out.println("main线程执行……");
    }

    //发起异步请求
    public static void runAsync() {
        CompletableFuture.runAsync(new Runnable() {
            @Override
            public void run() {
                System.out.println(Thread.currentThread().getName() + "你好runAsync");
//                int a = 10 / 0;
                //加了sleep后因为main方法结束，所以导致whenComplete不会执行,因为whenComplete是调用main线程执行的
//                SleepUtils.second(12);
            }
        }).whenComplete(new BiConsumer<Void, Throwable>() {
            @Override
            public void accept(Void acceptValue, Throwable throwable) {
                System.out.println("异步方法执行之后获取的值" + acceptValue);
                System.out.println("异步方法执行过程中产生异常" + throwable);
            }
        }).exceptionally(new Function<Throwable, Void>() {
            @Override
            public Void apply(Throwable throwable) {
                System.out.println("异步方法执行过程中exceptionally接受的异常" + throwable);
                return null;
            }
        });
    }

}