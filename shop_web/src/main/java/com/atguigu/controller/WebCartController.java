package com.atguigu.controller;

import com.atguigu.client.CartFeignClient;
import com.atguigu.client.ProductFeignClient;
import com.atguigu.entity.SkuInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


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
    @Autowired
    private ProductFeignClient productFeignClient;

    /**
     * 1.加入购物车的接口 ?skuId&skuNum=2
     *
     * @return
     */
    @RequestMapping("addCart.html")
    public String addCart(@RequestParam Long skuId, @RequestParam Long skuNum, Model model) {

        cartFeignClient.addCart(skuId, skuNum);
        SkuInfo skuInfo = productFeignClient.getSkuInfo(skuId);
        model.addAttribute("skuInfo", skuInfo);
        model.addAttribute("skuNum", skuNum);
        System.out.println("我是master");
        return "cart/addCart";
    }

    /**
     * 2.购物车列表
     */
    @RequestMapping("cart.html")
    public String cart() {

        return "cart/index";
    }
}
