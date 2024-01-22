package com.atguigu.service;

import com.atguigu.entity.OrderInfo;
import com.atguigu.enums.ProcessStatus;
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
    Long saveOrderAndDetail(OrderInfo orderInfo,String userId);

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

    /**
     * 6.查询订单基本信息与详细信息是为了接下来减库存使用
     *
     * @param orderId
     * @return
     */
    OrderInfo getOrderInfoAndDetail(Long orderId);

    /**
     * 7.修改订单状态
     *
     * @param orderInfo
     * @param status
     */
    void updateOrderStatus(OrderInfo orderInfo, ProcessStatus status);

    /**
     * 8.发送消息通知库存系统减库存
     * @param orderInfo
     */
    void sendMsgToWareHouse(OrderInfo orderInfo);

    /**
     * 9.根据仓库拆单
     * @param orderId
     * @param wareHouseIdSkuIdMapJson
     * @return
     */
    String splitOrder(Long orderId, String wareHouseIdSkuIdMapJson);

}
