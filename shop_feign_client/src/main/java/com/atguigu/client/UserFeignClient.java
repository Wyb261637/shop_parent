package com.atguigu.client;

import com.atguigu.entity.UserAddress;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * projectName: shop_parent
 *
 * @author: WangYiBing
 * time: 2023/12/10 17:50 周日
 * description:
 */
@FeignClient(value = "shop-user")
public interface UserFeignClient {
    /**
     * 1.根据用户id查询用户的收货地址信息
     *
     * @param userId
     * @return
     */
    @GetMapping("/user/getUserAddressByUserId/{userId}")
    List<UserAddress> getUserAddressByUserId(@PathVariable String userId);

}
