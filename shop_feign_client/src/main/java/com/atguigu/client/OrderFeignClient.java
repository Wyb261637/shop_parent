package com.atguigu.client;

import com.atguigu.result.RetVal;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * projectName: shop_parent
 *
 * @author: WangYiBing
 * time: 2023/12/10 19:02 周日
 * description:
 */
@FeignClient(value = "shop-order")
public interface OrderFeignClient {
    /**
     * 1.订单确认信息
     *
     * @return
     */
    @GetMapping("/order/confirm")
    RetVal confirm();
}
