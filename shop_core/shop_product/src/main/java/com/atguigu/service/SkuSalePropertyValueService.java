package com.atguigu.service;

import com.atguigu.entity.SkuSalePropertyValue;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * sku销售属性值 服务类
 * </p>
 *
 * @author WangYiBing
 * @since 2023-01-10
 */
public interface SkuSalePropertyValueService extends IService<SkuSalePropertyValue> {

    /**
     * 销售属性id的组合与skuId的对应关系
     * @param productId
     * @return
     */
    List<Map> getSalePropertyAndSkuIdMapping(Long productId);
}
