package com.atguigu.controller;


import com.atguigu.entity.CartInfo;
import com.atguigu.result.RetVal;
import com.atguigu.service.CartInfoService;
import com.atguigu.util.AuthContextHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * <p>
 * 购物车表 用户登录系统时更新冗余 前端控制器
 * </p>
 *
 * @author WangYiBing
 * @since 2023-11-21
 */
@RestController
@RequestMapping("/cart")
public class CartInfoController {
    @Autowired
    private CartInfoService cartInfoService;

    /**
     * 1.加入购物车
     */
    @GetMapping("addCart/{skuId}/{skuNum}")
    public RetVal addCart(@PathVariable Long skuId, @PathVariable Integer skuNum, HttpServletRequest request) {
        //在shop-cart中拿到用户id
        String oneOfUserId = getUserId(request);
        cartInfoService.addToCart(oneOfUserId, skuId, skuNum);
        return RetVal.ok();
    }

    /**
     * 2.购物车列表
     */
    @GetMapping("getCartList")
    public RetVal getCartList(HttpServletRequest request) {
        List<CartInfo> cartInfoList = cartInfoService.getCartList(request);
        return RetVal.ok(cartInfoList);
    }

    /**
     * 3.购物车勾选
     * @param isChecked
     * @param skuId
     * @param request
     */
    @GetMapping("checkCart/{skuId}/{isChecked}")
    public RetVal checkCart(@PathVariable Long skuId, @PathVariable Integer isChecked, HttpServletRequest request) {
        //在shop-cart中拿到用户id
        String oneOfUserId = getUserId(request);
        cartInfoService.checkCart(oneOfUserId,skuId,isChecked);
        return RetVal.ok();
    }

    /**
     * 4.删除购物车
     * @param skuId
     * @param request
     * @return
     */
    @DeleteMapping("deleteCart/{skuId}")
    public RetVal deleteCart(@PathVariable Long skuId, HttpServletRequest request) {
        //在shop-cart中拿到用户id
        String oneOfUserId = getUserId(request);
        cartInfoService.deleteCart(oneOfUserId,skuId);
        return RetVal.ok();
    }

    /**
     * 5.查询购物车勾选信息,其他微服务调用的不需要使用 RetVal
     * @param userId
     * @return
     */
    @GetMapping("getSelectCartInfo/{userId}")
    public List<CartInfo> getSelectCartInfo(@PathVariable String userId) {
        return  cartInfoService.getSelectCartInfo(userId);
    }

    private static String getUserId(HttpServletRequest request) {
        String oneOfUserId = "";
        String userId = AuthContextHolder.getUserId(request);

        if (StringUtils.isEmpty(userId)) {
            oneOfUserId = AuthContextHolder.getUserTempId(request);
        } else {
            oneOfUserId = userId;
        }
        return oneOfUserId;
    }
}

