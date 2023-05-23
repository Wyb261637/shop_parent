package com.atguigu.controller;


import com.atguigu.entity.ProductSpu;
import com.atguigu.result.RetVal;
import com.atguigu.service.BaseSalePropertyService;
import com.atguigu.service.ProductSpuService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 商品表 前端控制器
 * </p>
 *
 * @author WangYiBing
 * @since 2023-01-10
 */
@RestController
@RequestMapping("/product")
public class SpuController {

    @Autowired
    private ProductSpuService spuService;

    @Autowired
    private BaseSalePropertyService salePropertyService;

    /**
     * 1.根据商品分类id查询商品SPU列表 <a href="http://127.0.0.1/product/queryProductSpuByPage/1/10/62">...</a>
     *
     * @param currentPageNum
     * @param pageSize
     * @param category3Id
     * @return
     */
    @GetMapping("queryProductSpuByPage/{currentPageNum}/{pageSize}/{category3Id}")
    public RetVal queryProductSpuByPage(@PathVariable Long currentPageNum,
                                        @PathVariable Long pageSize,
                                        @PathVariable Long category3Id
    ) {
        IPage<ProductSpu> page = new Page<>(currentPageNum, pageSize);
        QueryWrapper<ProductSpu> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("category3_Id", category3Id);
        spuService.page(page, queryWrapper);
        return RetVal.ok();
    }

    /**
     * 2.查询所有的销售属性 <a href="http://127.0.0.1/product/queryAllSaleProperty">...</a>
     *
     * @return
     */
    @GetMapping("queryAllSaleProperty")
    public RetVal queryAllSaleProperty() {

        return RetVal.ok(salePropertyService.list(null));
    }


    /**
     * 3.添加SPU信息实战 <a href="http://127.0.0.1/product/saveProductSpu">...</a>
     *
     * @param productSpu
     * @return
     */
    @PostMapping("saveProductSpu")
    public RetVal saveProductSpu(@RequestBody ProductSpu productSpu) {
        spuService.saveProductSpu(productSpu);
        return RetVal.ok();
    }

}

