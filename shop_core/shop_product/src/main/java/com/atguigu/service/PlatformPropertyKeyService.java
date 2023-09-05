package com.atguigu.service;

import com.atguigu.entity.PlatformPropertyKey;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 属性表 服务类
 * </p>
 *
 * @author WangYiBing
 * @since 2022-07-20
 */
public interface PlatformPropertyKeyService extends IService<PlatformPropertyKey> {

    /**
     * 根据分类id查询平台属性信息
     * @param category1Id
     * @param category2Id
     * @param category3Id
     * @return
     */
    List<PlatformPropertyKey> getPlatformPropertyByCategoryId(Long category1Id, Long category2Id, Long category3Id);

    /**
     * 保存平台属性
     * @param platformPropertyKey
     * @return
     */
    boolean savePlatformProperty(PlatformPropertyKey platformPropertyKey);
    /**
     * 根据skuId查询商品的平台属性
     * @param skuId
     * @return
     */
    List<PlatformPropertyKey> getPlatformPropertyBySkuId(Long skuId);
}
