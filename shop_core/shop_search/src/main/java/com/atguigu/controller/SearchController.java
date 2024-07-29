package com.atguigu.controller;

import com.atguigu.result.RetVal;
import com.atguigu.search.Product;
import com.atguigu.search.SearchParam;
import com.atguigu.search.SearchResponseVo;
import com.atguigu.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.web.bind.annotation.*;

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
    @Autowired
    private SearchService searchService;

    /**
     * 1.创建索引与映射
     *
     * @return
     */
    @GetMapping("createIndex")
    public RetVal createIndex() {
        esRestTemplate.createIndex(Product.class);
        esRestTemplate.putMapping(Product.class);
        System.out.println("测试git回滚222！");
        return RetVal.ok();
    }

    /**
     * 2.商品的上架
     *
     * @param skuId
     * @return
     */
    @GetMapping("onSale/{skuId}")
    public RetVal onSale(@PathVariable Long skuId) {
        searchService.onSale(skuId);
        return RetVal.ok();
    }

    /**
     * 3.商品的下架
     *
     * @param skuId
     * @return
     */
    @GetMapping("offSale/{skuId}")
    public RetVal offSale(@PathVariable Long skuId) {
        searchService.offSale(skuId);
        return RetVal.ok();
    }

    /**
     * 4.商品的搜索
     *
     * @param searchParam
     * @return
     */
    @PostMapping("searchProduct")
    public RetVal searchProduct(@RequestBody SearchParam searchParam) {
        SearchResponseVo searchResponseVo = searchService.searchProduct(searchParam);
        return RetVal.ok(searchResponseVo);
    }
}
