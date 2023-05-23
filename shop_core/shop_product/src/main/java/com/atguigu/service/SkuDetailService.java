package com.atguigu.service;

import com.atguigu.entity.ProductSalePropertyKey;
import com.atguigu.entity.SkuInfo;

import java.util.List;
import java.util.Map;

/**
 * projectName: shop_parent
 *
 * @author: WangYiBing
 * time: 2023/1/13 11:53 周五
 * description:
 */
public interface SkuDetailService {

    /**
     * 根据skuId查询商品的基本信息
     * @param skuId
     * @return
     */
    SkuInfo getSkuInfo(Long skuId);

    /**
     * 销售属性id的组合与skuId的对应关系
     * @param productId
     * @return
     */
    Map<Object, Object> getSalePropertyAndSkuIdMapping(Long productId);

    /**
     * 获取所有的销售属性(spu全份)和sku的销售属性(一份)
     * @param productId
     * @param skuId
     * @return
     */
    List<ProductSalePropertyKey> getSpuSalePropertyAndSelected(Long productId, Long skuId);

}
