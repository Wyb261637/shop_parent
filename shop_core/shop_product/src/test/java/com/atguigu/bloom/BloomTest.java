package com.atguigu.bloom;

import org.junit.jupiter.api.Test;
import org.redisson.api.RBloomFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * projectName: shop_parent
 *
 * @author: WangYiBing
 * time: 2023/7/15 13:22 周六
 * description:
 */
@SpringBootTest
public class BloomTest {

    @Autowired
    private RBloomFilter<Long> skuBloomFilter;

    @Test
    public void bloomTest(){
        boolean flag1 = skuBloomFilter.contains(24L);
        System.out.println(flag1);
        boolean flag2 = skuBloomFilter.contains(66L);
        System.out.println(flag2);
    }
}
