package com.atguigu.client;

import com.atguigu.result.RetVal;
import com.atguigu.search.SearchParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * projectName: shop_parent
 *
 * @author: WangYiBing
 * time: 2023/9/5 21:51 周二
 * description:
 */
@FeignClient(value = "shop-search")
public interface SearchFeignClient {
    /**
     * 1.商品的上架
     * @param skuId
     * @return
     */
    @GetMapping("/search/onSale/{skuId}")
     RetVal onSale(@PathVariable Long skuId);
    /**
     * 2.商品的下架
     * @param skuId
     * @return
     */
    @GetMapping("/search/offSale/{skuId}")
     RetVal offSale(@PathVariable Long skuId);
    /**
     * 3.商品的搜索
     *
     * @param searchParam
     * @return
     */
    @PostMapping("/search/searchProduct")
     RetVal searchProduct(SearchParam searchParam);
}
