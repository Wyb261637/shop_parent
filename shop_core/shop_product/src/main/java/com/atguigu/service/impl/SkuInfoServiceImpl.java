package com.atguigu.service.impl;

import com.atguigu.entity.SkuImage;
import com.atguigu.entity.SkuInfo;
import com.atguigu.entity.SkuPlatformPropertyValue;
import com.atguigu.entity.SkuSalePropertyValue;
import com.atguigu.mapper.SkuInfoMapper;
import com.atguigu.service.SkuImageService;
import com.atguigu.service.SkuInfoService;
import com.atguigu.service.SkuPlatformPropertyValueService;
import com.atguigu.service.SkuSalePropertyValueService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * <p>
 * 库存单元表 服务实现类
 * </p>
 *
 * @author WangYiBing
 * @since 2023-01-10
 */
@Service
public class SkuInfoServiceImpl extends ServiceImpl<SkuInfoMapper, SkuInfo> implements SkuInfoService {
    @Autowired
    private SkuImageService skuImageService;

    @Autowired
    private SkuPlatformPropertyValueService skuPlatformPropertyValueService;

    @Autowired
    private SkuSalePropertyValueService skuSalePropertyValueService;

    /**
     * 保存商品SKU
     * 1.保存SKU基本信息
     * 2.保存SKU的平台属性
     * 3.保存SKU的销售属性
     * 4.保存SKU的图片信息
     *
     * @param skuInfo
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveSkuInfo(SkuInfo skuInfo) {
        //1.保存SKU基本信息
        baseMapper.insert(skuInfo);
        //2.保存SKU的平台属性
        List<SkuPlatformPropertyValue> skuPlatformPropertyValueList = skuInfo.getSkuPlatformPropertyValueList();
        if (!CollectionUtils.isEmpty(skuPlatformPropertyValueList)) {
            skuPlatformPropertyValueList.forEach(skuPlatformPropertyValue -> {
                skuPlatformPropertyValue.setSkuId(skuInfo.getId());
            });
            skuPlatformPropertyValueService.saveBatch(skuPlatformPropertyValueList);
        }
        //3.保存SKU的销售属性
        List<SkuSalePropertyValue> skuSalePropertyValueList = skuInfo.getSkuSalePropertyValueList();
        if (!CollectionUtils.isEmpty(skuSalePropertyValueList)){
            skuSalePropertyValueList.forEach(skuSalePropertyValue -> {
                skuSalePropertyValue.setSkuId(skuInfo.getId());
            });
            skuSalePropertyValueService.saveBatch(skuSalePropertyValueList);
        }
        //4.保存SKU的图片信息
        List<SkuImage> skuImageList = skuInfo.getSkuImageList();
        if (!CollectionUtils.isEmpty(skuImageList)) {
            skuImageList.forEach(skuImage -> {
                skuImage.setSkuId(skuInfo.getId());
            });
            skuImageService.saveBatch(skuImageList);
        }
    }
}
