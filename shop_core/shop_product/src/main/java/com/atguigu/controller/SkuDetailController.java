package com.atguigu.controller;

import com.atguigu.entity.BaseCategoryView;
import com.atguigu.entity.ProductSalePropertyKey;
import com.atguigu.entity.SkuInfo;
import com.atguigu.service.BaseCategoryViewService;
import com.atguigu.service.SkuDetailService;
import com.atguigu.service.SkuInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * projectName: shop_parent
 *
 * @author: WangYiBing
 * time: 2023/1/13 11:49 周五
 * description: 商品详情信息
 */
@Api(tags = "商品详情")
@RestController
@RequestMapping("/sku")
public class SkuDetailController {
    @Autowired
    private SkuDetailService skuDetailService;

    @Autowired
    private BaseCategoryViewService categoryViewService;

    @Autowired
    private SkuInfoService skuInfoService;


    /**
     * 1.根据skuId查询商品的基本信息
     *
     * @param skuId
     * @return
     */
    @GetMapping("getSkuInfo/{skuId}")
    @ApiOperation(value = "根据skuId查询商品的基本信息")
    public SkuInfo getSkuInfo(@PathVariable Long skuId) {
        return skuDetailService.getSkuInfo(skuId);
    }


    /**
     * 2.根据三级分类id查询商品的分类
     *
     * @param category3Id
     * @return
     */
    @GetMapping("getCategoryView/{category3Id}")
    public BaseCategoryView getCategoryView(@PathVariable Long category3Id) {
        return categoryViewService.getById(category3Id);
    }


    /**
     * 3.根据skuId查询商品的实时价格
     *
     * @param skuId
     * @return
     */
    @GetMapping("getSkuPrice/{skuId}")
    public BigDecimal getSkuPrice(@PathVariable Long skuId) {
        SkuInfo skuInfo = skuInfoService.getById(skuId);
        return skuInfo.getPrice();
    }

    /**
     * 4.销售属性id的组合与skuId的对应关系
     *
     * @param productId
     * @return
     */
    @GetMapping("getSalePropertyAndSkuIdMapping/{productId}")
    @ApiOperation(value = "销售属性id的组合与skuId的对应关系")
    public Map<Object, Object> getSalePropertyAndSkuIdMapping(@PathVariable Long productId) {
        return skuDetailService.getSalePropertyAndSkuIdMapping(productId);
    }


    /**
     * 5.获取所有的销售属性(spu全份)和sku的销售属性(一份)
     *
     * @param productId
     * @param skuId
     * @return
     */
    @GetMapping("getSpuSalePropertyAndSelected/{productId}/{skuId}")
    public List<ProductSalePropertyKey> getSpuSalePropertyAndSelected(@PathVariable Long productId, @PathVariable Long skuId) {
        return skuDetailService.getSpuSalePropertyAndSelected(productId, skuId);
    }
}
