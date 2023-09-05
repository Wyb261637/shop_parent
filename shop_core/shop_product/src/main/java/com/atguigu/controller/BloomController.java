package com.atguigu.controller;

import com.atguigu.entity.SkuInfo;
import com.atguigu.service.SkuInfoService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.redisson.api.RBloomFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

/**
 * projectName: shop_parent
 *
 * @author: WangYiBing
 * time: 2023/7/15 13:01 周六
 * description:
 */
@RestController
@RequestMapping("init")
public class BloomController {

    @Autowired
    private SkuInfoService skuInfoService;

    @Autowired
    private RBloomFilter<Long> skuBloomFilter;

    @GetMapping("/sku/bloom")
    public String skuBloom() {
        //实现数据统一
        skuBloomFilter.delete();
        skuBloomFilter.tryInit(10000,0.001);
        //加载数据库中的所有数据id
        QueryWrapper<SkuInfo> wrapper = new QueryWrapper<>();
        wrapper.select("id");
        List<SkuInfo> skuInfoList = skuInfoService.list(wrapper);
        for (SkuInfo skuInfo : skuInfoList) {
            Long skuId = skuInfo.getId();
            //以HASH散列到布隆过滤器里面
            skuBloomFilter.add(skuId);
        }
        return "success";
    }
}
