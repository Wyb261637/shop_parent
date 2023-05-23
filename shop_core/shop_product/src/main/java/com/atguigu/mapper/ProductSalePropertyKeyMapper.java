package com.atguigu.mapper;

import com.atguigu.entity.ProductSalePropertyKey;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * <p>
 * spu销售属性 Mapper 接口
 * </p>
 *
 * @author WangYiBing
 * @since 2023-01-10
 */
public interface ProductSalePropertyKeyMapper extends BaseMapper<ProductSalePropertyKey> {

    /**
     * 根据spuId查询所有的销售属性
     * @param spuId
     * @return
     */
    List<ProductSalePropertyKey> querySalePropertyByProductId(Long spuId);

    /**
     * 获取所有的销售属性(spu全份)和sku的销售属性(一份)
     * @param productId
     * @param skuId
     * @return
     */
    List<ProductSalePropertyKey> getSpuSalePropertyAndSelected(Long productId, Long skuId);
}
