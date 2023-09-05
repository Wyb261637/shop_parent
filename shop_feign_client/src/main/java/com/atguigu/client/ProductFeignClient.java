package com.atguigu.client;

import com.atguigu.entity.*;
import com.atguigu.result.RetVal;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * projectName: shop_parent
 *
 * @author: WangYiBing
 * time: 2023/1/13 11:15 周五
 * description: 商品服务
 */
@FeignClient(value = "shop-product")
public interface ProductFeignClient {

    /**
     * 1.根据skuId查询商品的基本信息
     *
     * @param skuId
     * @return
     */
    @GetMapping("/sku/getSkuInfo/{skuId}")
    SkuInfo getSkuInfo(@PathVariable Long skuId);

    /**
     * 2.根据三级分类id查询商品的分类
     *
     * @param category3Id
     * @return
     */
    @GetMapping("/sku/getCategoryView/{category3Id}")
    BaseCategoryView getCategoryView(@PathVariable Long category3Id);

    /**
     * 3.根据skuId查询商品的实时价格
     *
     * @param skuId
     * @return
     */
    @GetMapping("/sku/getSkuPrice/{skuId}")
    BigDecimal getSkuPrice(@PathVariable Long skuId);

    /**
     * 4.销售属性id的组合与skuId的对应关系
     *
     * @param productId
     * @return
     */
    @GetMapping("/sku/getSalePropertyAndSkuIdMapping/{productId}")
    Map<Object, Object> getSalePropertyAndSkuIdMapping(@PathVariable Long productId);


    /**
     * 5.获取所有的销售属性(spu全份)和sku的销售属性(一份)
     *
     * @param productId
     * @param skuId
     * @return
     */
    @GetMapping("/sku/getSpuSalePropertyAndSelected/{productId}/{skuId}")
    List<ProductSalePropertyKey> getSpuSalePropertyAndSelected(@PathVariable Long productId, @PathVariable Long skuId);

    /**
     * 6.首页商品分类数据信息显示
     * @param
     * @return
     */
    @GetMapping("/product/getIndexCategory")
     RetVal getIndexCategory();
    /**
     * 7.根据id查询品牌信息
     *
     * @param brandId
     * @return
     */
    @GetMapping("/product/brand/getBrandById/{brandId}")
     BaseBrand getBrandById(@PathVariable Long brandId);
    /**
     * 8.根据skuId查询商品的平台属性
     * @param skuId
     * @return
     */
    @GetMapping("/product/getPlatformPropertyBySkuId/{skuId}")
     List<PlatformPropertyKey> getPlatformPropertyBySkuId(@PathVariable Long skuId);
}
