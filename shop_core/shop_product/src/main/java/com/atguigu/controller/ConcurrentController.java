package com.atguigu.controller;

import com.atguigu.service.BaseBrandService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * projectName: shop_parent
 *
 * @author: WangYiBing
 * time: 2023/1/9 21:54 周一
 * description: 分布式锁测试
 */
@RestController
@RequestMapping("/product")
@Api(tags = "分布式锁测试")
public class ConcurrentController {
    @Autowired
    private BaseBrandService baseBrandService;


    /**
     * 分布式锁测试
     *
     * @return
     */
    @GetMapping ("setNum")
    @ApiOperation(value = "分布式锁测试")
    public String setNum() {
         baseBrandService.setNum();
        return "success";
    }

}
