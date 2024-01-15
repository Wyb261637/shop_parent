package com.atguigu.mapper;

import com.atguigu.entity.TOrder;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author WangYiBing
 * @since 2024-01-03
 */
public interface TOrder1Mapper extends BaseMapper<TOrder> {

    List<TOrder> queryOrderByCondition(@Param("userId") Long userId, @Param("orderId") Long orderId);
}
