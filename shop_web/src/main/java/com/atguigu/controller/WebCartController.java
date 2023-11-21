package com.atguigu.controller;

import com.atguigu.client.CartFeignClient;
import com.atguigu.util.AuthContextHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;


/**
 * projectName: shop_parent
 *
 * @author: WangYiBing
 * time: 2023/11/21 21:29 周二
 * description:
 */
@Controller
public class WebCartController {

    @Autowired
    private CartFeignClient cartFeignClient;

    /**
     * 1.加入购物车的接口 ?skuId&skuNum=2
     *
     * @return
     */
    @RequestMapping("addCart.html")
    public String addCart(@RequestParam Long skuId, @RequestParam Long skuNum, HttpServletRequest request){
        String userId = AuthContextHolder.getUserId(request);
        cartFeignClient.addCart(skuId,skuNum);
        return "cart/addCart";
    }
}
