package com.atguigu.cache;

import java.lang.annotation.*;

/**
 * projectName: shop_parent
 *
 * @author: WangYiBing
 * time: 2023/7/16 10:32 周日
 * description:
 */

@Target({ElementType.TYPE, ElementType.METHOD}) //该注解只能用在哪些地方
@Retention(RetentionPolicy.RUNTIME)//该注解的声明周期，表示只到运行周期就结束
public @interface ShopCache {
    String value() default "cache";
    //定义一个缓存前缀 目的：该缓存属于哪个部分的缓存
    String prefix() default "cache";
    //是否需要开启布隆过滤器
    boolean enableBloom() default true;
}
