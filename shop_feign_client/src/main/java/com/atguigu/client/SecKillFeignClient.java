package com.atguigu.client;

import com.atguigu.entity.SeckillProduct;
import com.atguigu.result.RetVal;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * projectName: shop_parent
 *
 * @author: WangYiBing
 * time: 2024/2/17 21:55 周六
 * description:
 */
@FeignClient(value = "shop-seckill")
public interface SecKillFeignClient {
    /**
     * 1.秒杀商品列表显示
     *
     * @return
     */
    @GetMapping("/seckill/queryAllSecKillProduct")
    List<SeckillProduct> queryAllSecKillProduct();

    /**
     * 2.单个秒杀商品详情
     *
     * @param skuId
     * @return
     */
    @GetMapping("/seckill/querySecKillProductById/{skuId}")
    SeckillProduct querySecKillProductById(@PathVariable Long skuId);

    /**
     * 3.返回秒杀页面需要的数据
     *
     * @return
     */
    @GetMapping("/seckill/seckillConfirm")
    RetVal seckillConfirm();
}
