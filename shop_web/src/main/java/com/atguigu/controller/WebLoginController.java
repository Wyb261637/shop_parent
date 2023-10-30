package com.atguigu.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import springfox.documentation.schema.Model;

/**
 * projectName: shop_parent
 *
 * @author: WangYiBing
 * time: 2023/10/30 20:43 周一
 * description:
 */
@Controller
public class WebLoginController {

    @RequestMapping("login.html")
    public String login(Model model) {
        return "login";
    }
}
