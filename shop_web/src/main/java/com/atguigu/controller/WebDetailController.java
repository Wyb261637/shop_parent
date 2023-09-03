package com.atguigu.controller;

import com.alibaba.fastjson.JSON;
import com.atguigu.client.ProductFeignClient;
import com.atguigu.entity.BaseCategoryView;
import com.atguigu.entity.ProductSalePropertyKey;
import com.atguigu.entity.SkuInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * projectName: shop_parent
 *
 * @author: WangYiBing
 * time: 2023/1/13 11:09 周五
 * description: Web页面详情显示
 */
@Controller
public class WebDetailController {

    @Autowired
    private ProductFeignClient productFeignClient;

    @Autowired
    private ThreadPoolExecutor myThreadPoolExecutor;

    @RequestMapping("{skuId}.html")
    public String getSkuDetail(@PathVariable Long skuId, Model model) {
        //3.根据skuId查询商品的实时价格
        CompletableFuture<Void> priceFuture = CompletableFuture.runAsync(() -> {
            BigDecimal skuPrice = productFeignClient.getSkuPrice(skuId);
            model.addAttribute("price", skuPrice);
            System.out.println(Thread.currentThread().getName() + "：线程3");
        },myThreadPoolExecutor);

        //1.根据skuId查询商品的基本信息
        CompletableFuture<SkuInfo> skuInfoFuture = CompletableFuture.supplyAsync(() -> {
            SkuInfo skuInfo = productFeignClient.getSkuInfo(skuId);
            model.addAttribute("skuInfo", skuInfo);
            System.out.println(Thread.currentThread().getName() + "：线程1");
            return skuInfo;
        },myThreadPoolExecutor);

        //2.根据三级分类id查询商品的分类
        CompletableFuture<Void> categoryViewFuture = skuInfoFuture.thenAcceptAsync((SkuInfo skuInfo) -> {
            BaseCategoryView categoryView = productFeignClient.getCategoryView(skuInfo.getCategory3Id());
            model.addAttribute("categoryView", productFeignClient.getCategoryView(skuInfo.getCategory3Id()));
            System.out.println(Thread.currentThread().getName() + "：线程2");
        },myThreadPoolExecutor);

        //4.销售属性id的组合与skuId的对应关系
        CompletableFuture<Void> salePropertyFuture = skuInfoFuture.thenAcceptAsync((SkuInfo skuInfo) -> {
            Map<Object, Object> salePropertyAndSkuIdMapping = productFeignClient.getSalePropertyAndSkuIdMapping(skuInfo.getProductId());
            model.addAttribute("salePropertyValueIdJson", JSON.toJSONString(salePropertyAndSkuIdMapping));
            System.out.println(Thread.currentThread().getName() + "：线程4");
        },myThreadPoolExecutor);

        //5.获取所有的销售属性(spu全份)和sku的销售属性(一份)
        CompletableFuture<Void> spuSalePropertyFuture = skuInfoFuture.thenAcceptAsync((SkuInfo skuInfo) -> {
            List<ProductSalePropertyKey> spuSalePropertyList = productFeignClient.getSpuSalePropertyAndSelected(skuInfo.getProductId(), skuId);
            model.addAttribute("spuSalePropertyList", spuSalePropertyList);
            System.out.println(Thread.currentThread().getName() + "：线程5");
        },myThreadPoolExecutor);

        //代表每一个异步请求都要执行完才返回到页面
        CompletableFuture.allOf(priceFuture,
                skuInfoFuture,
                categoryViewFuture,
                salePropertyFuture,
                spuSalePropertyFuture).join();
        return "detail/index";
    }
}
