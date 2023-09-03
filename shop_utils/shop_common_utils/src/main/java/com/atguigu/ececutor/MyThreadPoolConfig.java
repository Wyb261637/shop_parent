package com.atguigu.ececutor;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * projectName: shop_parent
 *
 * @author: WangYiBing
 * time: 2023/9/3 9:36 周日
 * description:
 */
@Configuration
@EnableConfigurationProperties(MyThreadProperties.class)
public class MyThreadPoolConfig {
    @Resource
    private MyThreadProperties myThreadProperties;
    /**
     * 高并发的情况下150个线程进来
     * 16个线程会立即得到处理
     * 扩展16个线程去继续处理
     * 100个线程会进入到队列中去
     * 还剩18个线程会采用拒绝策略
     *
     * ArrayBlockingQueue
     *      空间碎片问题，导致内存空间不连续
     * LinkedBlockingQueue
     *       不会引起空间碎片问题
     */
    @Bean
    public ThreadPoolExecutor myExecutor() {
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
                myThreadProperties.getCorePoolSize(),
                myThreadProperties.getMaximumPoolSize(),
                myThreadProperties.getKeepAliveTime(),
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(myThreadProperties.queueLength),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.CallerRunsPolicy());
        return threadPoolExecutor;
    }

}
