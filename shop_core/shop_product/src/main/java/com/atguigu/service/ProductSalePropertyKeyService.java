package com.atguigu.service;

import com.atguigu.entity.ProductSalePropertyKey;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * spu销售属性 服务类
 * </p>
 *
 * @author WangYiBing
 * @since 2023-01-10
 */
public interface ProductSalePropertyKeyService extends IService<ProductSalePropertyKey> {

    /**
     * 根据spuId查询所有的销售属性
     * @param spuId
     * @return
     */
    List<ProductSalePropertyKey> querySalePropertyByProductId(Long spuId);
}
