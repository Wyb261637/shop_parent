package com.atguigu.service;

import com.atguigu.entity.SeckillProduct;
import com.atguigu.entity.UserSeckillSkuInfo;
import com.atguigu.result.RetVal;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author WangYiBing
 * @since 2024-01-30
 */
public interface SeckillProductService extends IService<SeckillProduct> {

    /**
     * 1.从缓存中拿到秒杀商品信息
     * @param skuId
     * @return
     */
    SeckillProduct getSeckillById(Long skuId);

    /**
     * 2.消费秒杀预下单里面的消息
     * @param userSeckillSkuInfo
     */
    void prepareSecKill(UserSeckillSkuInfo userSeckillSkuInfo);

    /**
     * 3.判断是否具备抢购资格
     * @param userId
     * @param skuId
     * @return
     */
    RetVal hasQualified(String userId, Long skuId);

    /**
     * 4.返回秒杀页面需要的数据
     * @param userId
     * @return
     */
    RetVal seckillConfirm(String userId);

}
