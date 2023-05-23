package com.atguigu.service.impl;


import com.atguigu.entity.ProductImage;
import com.atguigu.entity.ProductSalePropertyKey;
import com.atguigu.entity.ProductSalePropertyValue;
import com.atguigu.entity.ProductSpu;
import com.atguigu.mapper.ProductSpuMapper;
import com.atguigu.service.ProductImageService;
import com.atguigu.service.ProductSalePropertyKeyService;
import com.atguigu.service.ProductSalePropertyValueService;
import com.atguigu.service.ProductSpuService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * <p>
 * 商品表 服务实现类
 * </p>
 *
 * @author WangYiBing
 * @since 2023-01-10
 */
@Service
public class ProductSpuServiceImpl extends ServiceImpl<ProductSpuMapper, ProductSpu> implements ProductSpuService {

    @Autowired
    private ProductImageService productImageService;

    @Autowired
    private ProductSalePropertyValueService salePropertyValueService;

    @Autowired
    private ProductSalePropertyKeyService salePropertyKeyService;

    /**
     * 添加SPU信息
     * 1.保存SPU基本信息
     * 2.保存SPU图片信息
     * 3.保存SPU销售属性key
     * 4.保存SPU销售属性value
     *
     * @param productSpu
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveProductSpu(ProductSpu productSpu) {
        //1.保存SPU基本信息
        baseMapper.insert(productSpu);
        //2.保存SPU图片信息
        List<ProductImage> productImageList = productSpu.getProductImageList();
        if (!CollectionUtils.isEmpty(productImageList)) {
            productImageList.forEach(productImage -> {
                productImage.setProductId(productSpu.getId());
            });
            productImageService.saveBatch(productImageList);
            //3.保存SPU销售属性key
            List<ProductSalePropertyKey> productSalePropertyKeyList = productSpu.getSalePropertyKeyList();
            if (!CollectionUtils.isEmpty(productSalePropertyKeyList)) {
                productSalePropertyKeyList.forEach(productSalePropertyKey -> {
                    productSalePropertyKey.setProductId(productSpu.getId());
                    //4.保存SPU销售属性value
                    List<ProductSalePropertyValue> salePropertyValueList = productSalePropertyKey.getSalePropertyValueList();
                    if (!CollectionUtils.isEmpty(salePropertyValueList)) {
                        salePropertyValueList.forEach(salePropertyValue -> {
                            salePropertyValue.setProductId(productSpu.getId());
                            salePropertyValue.setSalePropertyKeyName(productSalePropertyKey.getSalePropertyKeyName());
                        });
                        salePropertyValueService.saveBatch(salePropertyValueList);
                    }
                });
                salePropertyKeyService.saveBatch(productSalePropertyKeyList);
            }
        }
    }
}
