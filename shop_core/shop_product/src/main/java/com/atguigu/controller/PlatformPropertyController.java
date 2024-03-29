package com.atguigu.controller;


import com.atguigu.entity.PlatformPropertyKey;
import com.atguigu.entity.PlatformPropertyValue;
import com.atguigu.result.RetVal;
import com.atguigu.service.PlatformPropertyKeyService;
import com.atguigu.service.PlatformPropertyValueService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 属性表 前端控制器
 * </p>
 *
 * @author WangYiBing
 * @since 2022-07-20
 */
@RestController
@RequestMapping("/product")
public class PlatformPropertyController {

    @Autowired
    private PlatformPropertyKeyService platformPropertyKeyService;

    @Autowired
    private PlatformPropertyValueService platformPropertyValueService;

    /**
     * 1.根据分类id查询平台属性信息
     *
     * @param category1Id
     * @param category2Id
     * @param category3Id
     * @return
     */
    @GetMapping("getPlatformPropertyByCategoryId/{category1Id}/{category2Id}/{category3Id}")
    public RetVal getPlatformPropertyByCategoryId(@PathVariable Long category1Id,
                                                  @PathVariable Long category2Id,
                                                  @PathVariable Long category3Id) {
        List<PlatformPropertyKey> platformPropertyKeyList = platformPropertyKeyService.getPlatformPropertyByCategoryId(category1Id, category2Id, category3Id);
        return RetVal.ok(platformPropertyKeyList);
    }


    /**
     * 2.根据平台属性keyId查询平台属性值
     * @param propertyKeyId
     * @return
     */
    @GetMapping("getPropertyValueByPropertyKeyId/{propertyKeyId}")
    public RetVal getPropertyValueByPropertyKeyId(@PathVariable Long propertyKeyId) {
        QueryWrapper<PlatformPropertyValue> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("property_key_id", propertyKeyId);
        List<PlatformPropertyValue> list = platformPropertyValueService.list(queryWrapper);
        return RetVal.ok(list);
    }


    /**
     * 3.保存平台属性
     * @param platformPropertyKey
     * @return
     */
    @PostMapping("savePlatformProperty")
    public RetVal savePlatformProperty(@RequestBody PlatformPropertyKey platformPropertyKey) {
        boolean flag = platformPropertyKeyService.savePlatformProperty(platformPropertyKey);
        if (flag) {
            return RetVal.ok();
        } else {
            return RetVal.fail();
        }
    }
    /**
     * 4.根据skuId查询商品的平台属性
     * @param skuId
     * @return
     */
    @GetMapping("getPlatformPropertyBySkuId/{skuId}")
    public List<PlatformPropertyKey> getPlatformPropertyBySkuId(@PathVariable Long skuId) {
        return platformPropertyKeyService.getPlatformPropertyBySkuId(skuId);

    }
}

