package com;

import com.atguigu.async.FutureDemo01;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutionException;

/**
 * projectName: shop_parent
 *
 * @author: WangYiBing
 * time: 2023/9/2 18:49 周六
 * description:
 */
public class TestDemo {
    @Test
    public void test() throws ExecutionException, InterruptedException {
        FutureDemo01.supplyAsync();
    }
}
