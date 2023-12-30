package com.atguigu.controller;

import com.atguigu.client.OrderFeignClient;
import com.atguigu.result.RetVal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

/**
 * projectName: shop_parent
 *
 * @author: WangYiBing
 * time: 2023/12/10 16:43 周日
 * description:
 */
@Controller
public class WebOrderController {

    @Autowired
    private OrderFeignClient orderFeignClient;

    /**
     * 1.跳转到订单确认页面
     *
     * @param model
     * @return
     */
    @RequestMapping("confirm.html")
    public String confirm(Model model) {
        RetVal<Map<String,Object>> retVal = orderFeignClient.confirm();
        model.addAllAttributes(retVal.getData());
        return "order/confirm";
    }
}
