package com.atguigu.controller;

import com.atguigu.client.OrderFeignClient;
import com.atguigu.entity.OrderInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * projectName: shop_parent
 *
 * @author: WangYiBing
 * time: 2024/1/6 10:07 周六
 * description:
 */
@Controller
public class WebPayController {
    @Autowired

    private OrderFeignClient orderFeignClient;

    /**
     * 1.跳转到支付宝支付页面
     *
     * @param model
     * @param orderId
     * @return
     */
    @RequestMapping("pay.html")
    public String pay(Model model, @RequestParam Long orderId) {
        OrderInfo orderInfo = orderFeignClient.getOrderInfoById(orderId);
        model.addAttribute("orderInfo", orderInfo);
        return "payment/pay";
    }

    /**
     * 2.跳转到支付宝支付成功的页面
     *
     * @return
     */
    @RequestMapping("alipay/success.html")
    public String success() {
        //需要做很多页面处理 -修改订单状态，减少库存
        return "payment/success";
    }
}
