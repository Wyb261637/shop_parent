package com.atguigu.controller;

import com.atguigu.result.RetVal;
import com.atguigu.search.Product;
import com.atguigu.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * projectName: shop_parent
 *
 * @author: WangYiBing
 * time: 2023/9/4 23:59 周一
 * description:
 */
@RestController
@RequestMapping("/search")
public class SearchController {
    @Autowired
    private ElasticsearchRestTemplate esRestTemplate;
    @Resource
    private SearchService searchService;
    /**
     * 1.创建索引与映射
     * @return
     */
    @GetMapping("createIndex")
    public RetVal createIndex() {
        esRestTemplate.createIndex(Product.class);
        esRestTemplate.putMapping(Product.class);
        return RetVal.ok();
    }
    //2.商品的上架
    @GetMapping("onSale/{skuId}")
    public RetVal onSale(@PathVariable Long skuId){
        searchService.onSale(skuId);
        return RetVal.ok();
    }
}
