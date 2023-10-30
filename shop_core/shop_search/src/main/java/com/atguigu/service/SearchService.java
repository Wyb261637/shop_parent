package com.atguigu.service;

import com.atguigu.search.SearchParam;
import com.atguigu.search.SearchResponseVo;

/**
 * projectName: shop_parent
 *
 * @author: WangYiBing
 * time: 2023/9/5 0:03 周二
 * description:
 */
public interface SearchService {
    void onSale(Long skuId);

    void offSale(Long skuId);

    SearchResponseVo searchProduct(SearchParam searchParam);

}
