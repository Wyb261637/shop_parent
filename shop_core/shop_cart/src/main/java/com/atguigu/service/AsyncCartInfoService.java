package com.atguigu.service;

import com.atguigu.entity.CartInfo;

/**
 * projectName: shop_parent
 *
 * @author: WangYiBing
 * time: 2023/12/10 11:38 周日
 * description:
 */
public interface AsyncCartInfoService {

    void saveCartInfo(CartInfo existCartInfo);

}
