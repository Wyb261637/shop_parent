package com.atguigu.controller;

import com.atguigu.entity.BaseBrand;
import com.atguigu.result.RetVal;
import com.atguigu.service.BaseBrandService;
import com.atguigu.utils.MinioUploader;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * projectName: shop_parent
 *
 * @author: WangYiBing
 * time: 2023/1/9 21:54 周一
 * description: 商品品牌
 */
@RestController
@RequestMapping("/product/brand")
public class BrandController {
    @Autowired
    private BaseBrandService baseBrandService;

    @Autowired
    private MinioUploader minioUploader;

    /**
     * 1.分页查询品牌列表 <a href="http://192.168.2.129/product/brand/queryBrandByPage/1/10">...</a>
     *
     * @param currentPageNum
     * @param pageSize
     * @return
     */
    @GetMapping("queryBrandByPage/{currentPageNum}/{pageSize}")
    public RetVal queryBrandByPage(@PathVariable Long currentPageNum, @PathVariable Long pageSize) {
        IPage<BaseBrand> page = new Page<>(currentPageNum, pageSize);
        baseBrandService.page(page, null);
        return RetVal.ok(page);
    }


    /**
     * 2.添加品牌 <a href="http://192.168.2.129/product/brand">...</a>
     *
     * @param baseBrand
     * @return
     */
    @PostMapping
    public RetVal saveBrand(@RequestBody BaseBrand baseBrand) {
        baseBrandService.save(baseBrand);
        return RetVal.ok();
    }

    /**
     * 3.根据id查询品牌信息 <a href="http://192.168.2.129/product/brand/4">...</a>
     *
     * @param brandId
     * @return
     */
    @GetMapping("{brandId}")
    public RetVal getBrandById(@PathVariable Long brandId) {
        return RetVal.ok(baseBrandService.getById(brandId));
    }

    /**
     * 4.更改品牌信息
     *
     * @param baseBrand
     * @return
     */
    @PutMapping
    public RetVal updateBrand(@RequestBody BaseBrand baseBrand) {
        baseBrandService.updateById(baseBrand);
        return RetVal.ok();
    }

    /**
     * 5.根据id删除品牌信息
     *
     * @param brandId
     * @return
     */
    @DeleteMapping("{brandId}")
    public RetVal deleteBrand(@PathVariable Long brandId) {
        baseBrandService.removeById(brandId);
        return RetVal.ok();
    }

    /**
     * 6.查询所有商品品牌
     *
     * @return
     */
    @GetMapping ("getAllBrand")
    public RetVal getAllBrand() {
        List<BaseBrand> list = baseBrandService.list(null);
        return RetVal.ok(list);
    }

    /**
     * 7.上传文件
     * @param file
     * @return
     * @throws Exception
     */
    @PostMapping("fileUpload")
    public RetVal fileUpload(MultipartFile file) throws Exception {
        return RetVal.ok(minioUploader.uploadFile(file));
    }
    /**
     * 8.根据分类id查询品牌列表
     * @param category1Id
     * @param category2Id
     * @param category3Id
     * @return
     */
    @GetMapping("getCategoryByCategoryId/{category1Id}/{category2Id}/{category3Id}")
    public RetVal getCategoryByCategoryId(@PathVariable Long category1Id,
                                                  @PathVariable Long category2Id,
                                                  @PathVariable Long category3Id){
        List<BaseBrand> baseBrandList=baseBrandService.getCategoryByCategoryId(category1Id,category2Id,category3Id);
        return RetVal.ok(baseBrandList);
    }
}
