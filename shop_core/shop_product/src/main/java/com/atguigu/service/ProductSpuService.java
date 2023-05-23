package com.atguigu.service;

import com.atguigu.entity.ProductSpu;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 商品表 服务类
 * </p>
 *
 * @author WangYiBing
 * @since 2023-01-10
 */
public interface ProductSpuService extends IService<ProductSpu> {

    /**
     * 添加SPU信息
     * @param productSpu
     */
    void saveProductSpu(ProductSpu productSpu);
}
