package com.atguigu.consumer;

import com.atguigu.constant.MqConst;
import com.atguigu.entity.OrderInfo;
import com.atguigu.enums.OrderStatus;
import com.atguigu.enums.ProcessStatus;
import com.atguigu.service.OrderInfoService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * projectName: shop_parent
 *
 * @author: WangYiBing
 * time: 2023/12/29 23:48 周五
 * description:
 */
@Component
public class OrderConsumer {

    @Autowired
    private OrderInfoService orderInfoService;

    /**
     * 1.超时取消未支付订单
     *
     * @param orderId
     */
    @RabbitListener(queues = MqConst.CANCEL_ORDER_QUEUE)
    public void cancelOrder(long orderId) {
        OrderInfo orderInfo = orderInfoService.getById(orderId);
        orderInfo.setOrderStatus(OrderStatus.CLOSED.name());
        orderInfo.setProcessStatus(ProcessStatus.CLOSED.name());
        orderInfoService.updateById(orderInfo);
        //TODO 后续还有其他事情要做
    }
}
