package com.atguigu.service.impl;

import com.atguigu.client.ProductFeignClient;
import com.atguigu.entity.SkuInfo;
import com.atguigu.mapper.ProductMapper;
import com.atguigu.search.Product;
import com.atguigu.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

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
        if(skuInfo!=null) {
            product.setId(skuInfo.getId());
            product.setProductName(skuInfo.getSkuName());
            product.setCreateTime(new Date());
            product.setPrice(skuInfo.getPrice().doubleValue());
            product.setDefaultImage(skuInfo.getSkuDefaultImg());
        }
        //b.品牌信息
        //c.商品的分类信息
        //d.根据skuId查询商品的平台属性
        //e.存储到ES中
        productMapper.save(product);
    }
}
