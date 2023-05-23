package com.atguigu.mapper;

import com.atguigu.entity.SkuSalePropertyValue;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.MapKey;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * sku销售属性值 Mapper 接口
 * </p>
 *
 * @author WangYiBing
 * @since 2023-01-10
 */
public interface SkuSalePropertyValueMapper extends BaseMapper<SkuSalePropertyValue> {

    /**
     * 销售属性id的组合与skuId的对应关系
     * @param productId
     * @return
     */
    @MapKey("id") //指定一个字段作为返回Map中的key，这里一般也就是使用唯一键来做key
    List<Map> getSalePropertyAndSkuIdMapping(Long productId);
}
