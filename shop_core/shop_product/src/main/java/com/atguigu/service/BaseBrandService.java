package com.atguigu.service;

import com.atguigu.entity.BaseBrand;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 品牌表 服务类
 * </p>
 *
 * @author WangYiBing
 * @since 2023-01-09
 */
public interface BaseBrandService extends IService<BaseBrand> {

    /**
     * 根据分类id查询品牌列表
     * @param category1Id
     * @param category2Id
     * @param category3Id
     * @return
     */
    List<BaseBrand> getCategoryByCategoryId(Long category1Id, Long category2Id, Long category3Id);

    /**
     * 分布式锁测试
     */
    void setNum();

    /**
     * 添加品牌
     * @param baseBrand
     */
//    void saveBrand(BaseBrand baseBrand);
}
