package com.atguigu.service.impl;

import com.alibaba.nacos.client.naming.utils.CollectionUtils;
import com.atguigu.client.ProductFeignClient;
import com.atguigu.entity.BaseBrand;
import com.atguigu.entity.BaseCategoryView;
import com.atguigu.entity.PlatformPropertyKey;
import com.atguigu.entity.SkuInfo;
import com.atguigu.mapper.ProductMapper;
import com.atguigu.search.Product;
import com.atguigu.search.SearchPlatformProperty;
import com.atguigu.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * projectName: shop_parent
 *
 * @author: WangYiBing
 * time: 2023/9/5 0:03 周二
 * description:
 */
@Service
public class SearchServiceImpl implements SearchService {
    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private ProductFeignClient productFeignClient;

    @Override
    public void onSale(Long skuId) {
        Product product = new Product();
        //a.商品基本信息
        SkuInfo skuInfo = productFeignClient.getSkuInfo(skuId);
        if (skuInfo != null) {
            product.setId(skuInfo.getId());
            product.setProductName(skuInfo.getSkuName());
            product.setCreateTime(new Date());
            product.setPrice(skuInfo.getPrice().doubleValue());
            product.setDefaultImage(skuInfo.getSkuDefaultImg());
            //b.品牌信息
            Long brandId = skuInfo.getBrandId();
            BaseBrand brand = productFeignClient.getBrandById(brandId);
            if (brand != null) {
                product.setBrandId(brand.getId());
                product.setBrandName(brand.getBrandName());
                product.setBrandLogoUrl(brand.getBrandLogoUrl());
            }
            //c.商品的分类信息
            Long category3Id = skuInfo.getCategory3Id();
            BaseCategoryView categoryView = productFeignClient.getCategoryView(category3Id);
            if (categoryView != null) {
                product.setCategory1Id(categoryView.getCategory1Id());
                product.setCategory1Name(categoryView.getCategory1Name());
                product.setCategory2Id(categoryView.getCategory2Id());
                product.setCategory2Name(categoryView.getCategory2Name());
                product.setCategory3Id(categoryView.getCategory3Id());
                product.setCategory3Name(categoryView.getCategory3Name());
            }
            //d.根据skuId查询商品的平台属性
            List<PlatformPropertyKey> platformPropertyList = productFeignClient.getPlatformPropertyBySkuId(skuId);
            if (!CollectionUtils.isEmpty(platformPropertyList)) {
                List<SearchPlatformProperty> searchPlatformList = platformPropertyList.stream().map(platformPropertyKey -> {
                    SearchPlatformProperty searchPlatformProperty = new SearchPlatformProperty();
                    //平台属性id
                    searchPlatformProperty.setPropertyKeyId(platformPropertyKey.getId());
                    //平台属性值
                    String propertyValue = platformPropertyKey.getPropertyValueList().get(0).getPropertyValue();
                    searchPlatformProperty.setPropertyValue(propertyValue);
                    return searchPlatformProperty;
                }).collect(toList());
                product.setPlatformProperty(searchPlatformList);
            }
        }

        //e.存储到ES中
        productMapper.save(product);
    }

    @Override
    public void offSale(Long skuId) {
        //从es中删除
        productMapper.deleteById(skuId);
    }
}
