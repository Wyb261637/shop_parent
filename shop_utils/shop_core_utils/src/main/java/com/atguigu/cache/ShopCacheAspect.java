package com.atguigu.cache;

import com.atguigu.constant.RedisConst;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

/**
 * projectName: shop_parent
 *
 * @author: WangYiBing
 * time: 2023/7/16 11:00 周日
 * description:
 */
@Component
@Aspect
public class ShopCacheAspect {
    @Resource
    private RedissonClient redissonClient;
    @Resource
    private RBloomFilter skuBloomFilter;
    @Resource
    private RedisTemplate redisTemplate;

    @Around("@annotation(com.atguigu.cache.ShopCache)")
    public Object cacheAroundAdvice(ProceedingJoinPoint target) throws Throwable {
        //开启本地锁
//        Object[] methodParameters = target.getArgs();
//        Object methodParameter = null;
//        if (methodParameters.length > 0) {
//            methodParameter = methodParameters[0];
//        }
//        //通过target拿到目标方法
//        MethodSignature methodSignature = (MethodSignature) target.getSignature();
//        Method targetMethod = methodSignature.getMethod();
//        //通过目标方法获取注解
//        ShopCache shopCache = targetMethod.getAnnotation(ShopCache.class);
//        //通过注解获取缓存key的前缀
//        String prefix = shopCache.prefix();
//        boolean enabledBloom = shopCache.enableBloom();
//        //需要先查询redis是否有数据，没有的话再查询数据库，然后把数据添加到redis中去
//        //创建缓存名 skuInfo:24,price:24
//        String cacheKey = prefix + ":" + methodParameter + RedisConst.SKUKEY_SUFFIX;
//        //从缓存中查询数据
//        Object cacheFromRedis = redisTemplate.opsForValue().get(cacheKey);
//        //判断是否为空
//        if (cacheFromRedis == null) {
//            String lockKey = "lock-" + methodParameter;
//            //赋值一个空值对象
//            Object queryFromDb = new Object();
//            //分布式锁改用本地锁
//            synchronized (lockKey.intern()) {
//                queryFromDb = target.proceed();
//                //c.放入数据到缓存
//                redisTemplate.opsForValue().set(cacheKey, queryFromDb, RedisConst.SKUKEY_TIMEOUT, TimeUnit.SECONDS);
//            }
//
//            return queryFromDb;
//        }
//        return cacheFromRedis;
//    }
        //开启分布式锁
        Object[] methodParameters = target.getArgs();
        Object methodParameter = null;
        if (methodParameters.length > 0) {
            methodParameter = methodParameters[0];
        }

        //通过target拿到目标方法
        MethodSignature methodSignature = (MethodSignature) target.getSignature();
        Method targetMethod = methodSignature.getMethod();
        //通过目标方法获取注解
        ShopCache shopCache = targetMethod.getAnnotation(ShopCache.class);
        //通过注解获取缓存key的前缀
        String prefix = shopCache.prefix();
        boolean enabledBloom = shopCache.enableBloom();
        //需要先查询redis是否有数据，没有的话再查询数据库，然后把数据添加到redis中去
        //创建缓存名 skuInfo:24,price:24
        String cacheKey = prefix + ":" + methodParameter + RedisConst.SKUKEY_SUFFIX;
        //从缓存中查询数据
        Object cacheFromRedis = redisTemplate.opsForValue().get(cacheKey);
        //判断是否为空
        if (cacheFromRedis == null) {
            //加入分布式锁
            RLock lock = redissonClient.getLock("lock-" + methodParameter);
            lock.lock();
            try {
                //赋值一个空值对象
                Object queryFromDb = new Object();
                if (enabledBloom) {
                    //先去查询布隆过滤器是否有该数据
                    boolean flag = skuBloomFilter.contains(methodParameter);
                    if (flag) {
                        //b.如果缓存里面没有数据 从数据库中查
                        queryFromDb = target.proceed();
                    }
                } else {
                    queryFromDb = target.proceed();
                }
                //c.放入数据到缓存
                redisTemplate.opsForValue().set(cacheKey, queryFromDb, RedisConst.SKUKEY_TIMEOUT, TimeUnit.SECONDS);
                return queryFromDb;
            } finally {
                lock.unlock();
            }
        }
        return cacheFromRedis;
    }
}