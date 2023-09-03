package com.atguigu.controller;

import com.atguigu.client.ProductFeignClient;
import com.atguigu.result.RetVal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * projectName: shop_parent
 *
 * @author: WangYiBing
 * time: 2023/1/13 11:09 周五
 * description: Web页面详情显示
 */
@Controller
public class WebIndexController {
    @Autowired
    private ProductFeignClient productFeignClient;
    /**
     * 支持两种跳转方式：<a href="http://item.gmall.com/,http://item.gmall.com/index.html">...</a>
     */
    @RequestMapping({"/", "index.html"})
    public String index(Model model) {
        RetVal retVal = productFeignClient.getIndexCategory();
        model.addAttribute("list",retVal.getData());
        return "index/index";
    }
}
