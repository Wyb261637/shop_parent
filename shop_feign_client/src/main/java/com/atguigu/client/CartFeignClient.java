package com.atguigu.client;

import com.atguigu.result.RetVal;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * projectName: shop_parent
 *
 * @author: WangYiBing
 * time: 2023/11/21 23:08 周二
 * description:
 */
@FeignClient(value = "shop-cart")
public interface CartFeignClient {
    /**
     * 1.加入购物车
     * @param skuId
     * @param skuNum
     * @return
     */
    @GetMapping("/cart/addCart/{skuId}/{skuNum}")
     RetVal addCart(@PathVariable Long skuId, @PathVariable Long skuNum);
}
