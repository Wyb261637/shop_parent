package com.atguigu.executor;

import java.util.concurrent.*;

/**
 * projectName: shop_parent
 *
 * @author: WangYiBing
 * time: 2023/9/3 9:15 周日
 * description:
 */
public class ThreadPoolTest {
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
    public static void main(String[] args) {
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(16,
                32,
                50L,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(100),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.CallerRunsPolicy());
    }
}
