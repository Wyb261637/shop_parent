package com.atguigu.service;

import com.atguigu.entity.OrderInfo;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 订单表 订单表 服务类
 * </p>
 *
 * @author WangYiBing
 * @since 2023-12-10
 */
public interface OrderInfoService extends IService<OrderInfo> {
    /**
     * 1.提交订单
     *
     * @param orderInfo
     * @return
     */
    Long saveOrderAndDetail(OrderInfo orderInfo);

    /**
     * 2.生成流水号
     *
     * @param userId
     * @return
     */
    String generateTradeNo(String userId);

    /**
     * 3.把从redis中的tradeNo进行对比
     *
     * @param userId
     * @param tradeNoUI
     * @return
     */
    boolean compareTradeNo(String userId, String tradeNoUI);

    /**
     * 4.订单提交成功后还需要删除流水号
     *
     * @param userId
     */
    void deleteTradeNo(String userId);

    /**
     * 5.验证库存与方法
     *
     * @param userId
     * @param orderInfo
     * @return
     */
    String checkStockAndPrice(String userId, OrderInfo orderInfo);


}
