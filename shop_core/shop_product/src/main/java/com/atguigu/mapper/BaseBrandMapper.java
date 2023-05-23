package com.atguigu.mapper;

import com.atguigu.entity.BaseBrand;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * <p>
 * 品牌表 Mapper 接口
 * </p>
 *
 * @author WangYiBing
 * @since 2023-01-09
 */
public interface BaseBrandMapper extends BaseMapper<BaseBrand> {
    /**
     * 根据分类id查询品牌列表
     * @param category1Id
     * @param category2Id
     * @param category3Id
     * @return
     */
    List<BaseBrand> getCategoryByCategoryId(Long category1Id, Long category2Id, Long category3Id);
}
