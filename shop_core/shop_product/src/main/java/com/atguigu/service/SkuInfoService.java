package com.atguigu.service;

import com.atguigu.entity.SkuInfo;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 库存单元表 服务类
 * </p>
 *
 * @author WangYiBing
 * @since 2023-01-10
 */
public interface SkuInfoService extends IService<SkuInfo> {
    /**
     * 保存商品SKU
     * @param skuInfo
     */
    void saveSkuInfo(SkuInfo skuInfo);
}
