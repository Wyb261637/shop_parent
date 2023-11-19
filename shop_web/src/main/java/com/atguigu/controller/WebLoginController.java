package com.atguigu.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

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
    public String login(Model model, HttpServletRequest request) {
        String originalUrl = request.getParameter("originalUrl");
        model.addAttribute("originalUrl",originalUrl);
        return "login";
    }
}
