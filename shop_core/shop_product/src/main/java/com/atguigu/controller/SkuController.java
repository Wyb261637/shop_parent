package com.atguigu.controller;


import com.atguigu.constant.MqConst;
import com.atguigu.entity.ProductImage;
import com.atguigu.entity.ProductSalePropertyKey;
import com.atguigu.entity.SkuInfo;
import com.atguigu.result.RetVal;
import com.atguigu.service.ProductImageService;
import com.atguigu.service.ProductSalePropertyKeyService;
import com.atguigu.service.SkuInfoService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 库存单元表 前端控制器
 * </p>
 *
 * @author WangYiBing
 * @since 2023-01-10
 */
@RestController
@RequestMapping("/product")
public class SkuController {

    @Autowired
    private ProductImageService productImageService;

    @Autowired
    private SkuInfoService skuInfoService;

//    @Autowired
//    private SearchFeignClient searchFeignClient;

    @Autowired
    private ProductSalePropertyKeyService salePropertyKeyService;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * 1.根据spuId查询所有的spu图片 <a href="http://127.0.0.1/product/V/12">...</a>
     *
     * @param spuId
     * @return
     */
    @GetMapping("queryProductImageByProductId/{spuId}")
    public RetVal queryProductImageByProductId(@PathVariable Long spuId) {
        QueryWrapper<ProductImage> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("product_id", spuId);
        return RetVal.ok(productImageService.list(queryWrapper));
    }

    /**
     * 2.根据spuId查询所有的销售属性 <a href="http://127.0.0.1/product/querySalePropertyByProductId/12">...</a>
     *
     * @param spuId
     * @return
     */
    @GetMapping("querySalePropertyByProductId/{spuId}")
    public RetVal querySalePropertyByProductId(@PathVariable Long spuId) {
        List<ProductSalePropertyKey> salePropertyKeyList = salePropertyKeyService.querySalePropertyByProductId(spuId);
        return RetVal.ok(salePropertyKeyList);
    }


    /**
     * 3.保存商品SKU <a href="http://127.0.0.1/product/saveSkuInfo">...</a>
     *
     * @param skuInfo
     * @return
     */
    @PostMapping("saveSkuInfo")
    public RetVal saveSkuInfo(@RequestBody SkuInfo skuInfo) {
        skuInfoService.saveSkuInfo(skuInfo);
        return RetVal.ok();
    }


    /**
     * 4.商品SKU分页列表查询 <a href="http://127.0.0.1/product/querySkuInfoByPage/1/10">...</a>
     *
     * @param currentPageNum
     * @param pageSize
     * @return
     */
    @GetMapping("querySkuInfoByPage/{currentPageNum}/{pageSize}")
    public RetVal querySkuInfoByPage(@PathVariable Long currentPageNum, @PathVariable Long pageSize) {
        IPage<SkuInfo> page = new Page<>(currentPageNum, pageSize);
        skuInfoService.page(page, null);
        return RetVal.ok(page);
    }

    /**
     * 5.商品SKU下架 <a href="http://127.0.0.1/product/offSale/24">...</a>
     * @param skuId
     * @return
     */
    @GetMapping("offSale/{skuId}")
    public RetVal offSale(@PathVariable Long skuId) {
        SkuInfo skuInfo = new SkuInfo();
        skuInfo.setId(skuId);
        //0代表商品下架
        skuInfo.setIsSale(0);
        skuInfoService.updateById(skuInfo);
//        searchFeignClient.offSale(skuId);
        rabbitTemplate.convertAndSend(MqConst.ON_OFF_SALE_EXCHANGE,MqConst.OFF_SALE_ROUTING_KEY,skuId);
        return RetVal.ok();
    }


    /**
     * 6.商品SKU上架 <a href="http://127.0.0.1/product/onSale/24">...</a>
     * @param skuId
     * @return
     */
    @GetMapping("onSale/{skuId}")
    public RetVal onSale(@PathVariable Long skuId) {
        SkuInfo skuInfo = new SkuInfo();
        skuInfo.setId(skuId);
        //1代表商品上架
        skuInfo.setIsSale(1);
        skuInfoService.updateById(skuInfo);
//        searchFeignClient.onSale(skuId);
        rabbitTemplate.convertAndSend(MqConst.ON_OFF_SALE_EXCHANGE,MqConst.ON_SALE_ROUTING_KEY,skuId);
        return RetVal.ok();
    }
}

