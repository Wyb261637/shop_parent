package com.atguigu.job;

import com.atguigu.constant.MqConst;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * projectName: shop_parent
 *
 * @author: WangYiBing
 * time: 2024/2/27 23:38 周二
 * description:
 */
//开启定时任务
@EnableScheduling
@Component
public class ScheduleJob {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * 1.发送一个定时上架秒杀商品的消息
     */
    @Scheduled(cron = "* 0 1 * * ? ")
    public void taskEveryNight01() {
        // 发送的消息
        rabbitTemplate.convertAndSend(MqConst.SCAN_SECKILL_EXCHANGE, MqConst.SCAN_SECKILL_ROUTE_KEY, "xxx");
        System.out.println("执行上架任务！");
    }

    /**
     * 2.发送一个定时下架秒杀商品的消息
     */
    @Scheduled(cron = "* 0 0 * * ? ")
    public void task18() {
        rabbitTemplate.convertAndSend(MqConst.CLEAR_REDIS_EXCHANGE, MqConst.CLEAR_REDIS_ROUTE_KEY, "xxx");
        System.out.println("执行下架任务！");
    }
}
