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

    @RequestMapping("{skuId}.html")
    public String getSkuDetail(@PathVariable Long skuId, Model model){
        //1.根据skuId查询商品的基本信息
        SkuInfo skuInfo = productFeignClient.getSkuInfo(skuId);
        model.addAttribute("skuInfo",skuInfo);

        //2.根据三级分类id查询商品的分类
        BaseCategoryView categoryView = productFeignClient.getCategoryView(skuInfo.getCategory3Id());
        model.addAttribute("categoryView",categoryView);

        //3.根据skuId查询商品的实时价格
        BigDecimal skuPrice = productFeignClient.getSkuPrice(skuId);
        model.addAttribute("price",skuPrice);

        //4.销售属性id的组合与skuId的对应关系
        Map<Object, Object> salePropertyAndSkuIdMapping = productFeignClient.getSalePropertyAndSkuIdMapping(skuInfo.getProductId());
        model.addAttribute("salePropertyValueIdJson", JSON.toJSONString(salePropertyAndSkuIdMapping));

        //5.获取所有的销售属性(spu全份)和sku的销售属性(一份)
        List<ProductSalePropertyKey> spuSalePropertyList = productFeignClient.getSpuSalePropertyAndSelected(skuInfo.getProductId(), skuId);
        model.addAttribute("spuSalePropertyList",spuSalePropertyList);
        return "detail/index";
    }
}
