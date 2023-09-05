package com.atguigu.mapper;

import com.atguigu.entity.PlatformPropertyKey;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 属性表 Mapper 接口
 * </p>
 *
 * @author WangYiBing
 * @since 2022-07-20
 */
public interface PlatformPropertyKeyMapper extends BaseMapper<PlatformPropertyKey> {

    /**
     * 根据分类id查询平台属性信息
     * @param category1Id
     * @param category2Id
     * @param category3Id
     * @return
     */
    List<PlatformPropertyKey> getPlatformPropertyByCategoryId(@Param("category1Id") Long category1Id,@Param("category2Id")  Long category2Id,@Param("category3Id")  Long category3Id);
    /**
     * 根据skuId查询商品的平台属性
     * @param skuId
     * @return
     */
    List<PlatformPropertyKey> getPlatformPropertyBySkuId(Long skuId);
}
