package com.atguigu.controller;


import com.atguigu.client.CartFeignClient;
import com.atguigu.client.UserFeignClient;
import com.atguigu.entity.CartInfo;
import com.atguigu.entity.OrderDetail;
import com.atguigu.entity.OrderInfo;
import com.atguigu.entity.UserAddress;
import com.atguigu.result.RetVal;
import com.atguigu.service.OrderInfoService;
import com.atguigu.util.AuthContextHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 订单表 订单表 前端控制器
 * </p>
 *
 * @author WangYiBing
 * @since 2023-12-10
 */
@RestController
@RequestMapping("/order")
public class OrderInfoController {
    @Autowired
    private UserFeignClient userFeignClient;
    @Autowired
    private CartFeignClient cartFeignClient;
    @Autowired
    private OrderInfoService orderInfoService;

    /**
     * 1.订单确认信息
     *
     * @return
     */
    @GetMapping("confirm")
    public RetVal confirm(HttpServletRequest request) {
        String userId = AuthContextHolder.getUserId(request);
        //用户地址收货信息 shop-user
        List<UserAddress> userAddressList = userFeignClient.getUserAddressByUserId(userId);
        //订单的送货清单 shop-cart
        List<CartInfo> selectCartInfoList = cartFeignClient.getSelectCartInfo(userId);
        //商品的总件数与总金额
        List<OrderDetail> orderDetailList = new ArrayList<>();
        int totalNum = 0;
        BigDecimal totalMoney = new BigDecimal(0);
        if (!CollectionUtils.isEmpty(selectCartInfoList)) {
            for (CartInfo cartInfo : selectCartInfoList) {

                OrderDetail orderDetail = new OrderDetail();
                orderDetail.setSkuId(cartInfo.getSkuId());
                orderDetail.setSkuName(cartInfo.getSkuName());
                orderDetail.setImgUrl(cartInfo.getImgUrl());
                orderDetail.setSkuNum(cartInfo.getSkuNum().toString());
                //订单的价格 不拿实时价格
                orderDetail.setOrderPrice(cartInfo.getCartPrice());
                //订单总金额
                totalMoney = totalMoney.add(cartInfo.getCartPrice().multiply(new BigDecimal(cartInfo.getSkuNum())));
                //总数量
                totalNum += cartInfo.getSkuNum();
                orderDetailList.add(orderDetail);
            }
        }
        Map<String, Object> retMap = new HashMap<>();
        retMap.put("userAddressList", userAddressList);
        retMap.put("detailArrayList", orderDetailList);
        retMap.put("totalNum", totalNum);
        retMap.put("totalMoney", totalMoney);
        //生成一个流水号
        String tradeNo = orderInfoService.generateTradeNo(userId);
        retMap.put("tradeNo", tradeNo);
        return RetVal.ok(retMap);
    }

    /**
     * 2.提交订单
     *
     * @param orderInfo
     * @param request
     * @return
     */
    @PostMapping("submitOrder")
    public RetVal submitOrder(@RequestBody OrderInfo orderInfo, HttpServletRequest request) {
        String userId = AuthContextHolder.getUserId(request);
        //获取从页面传递过来的tradeNo
        String tradeNoUI = request.getParameter("tradeNo");
        //把从redis中的tradeNo进行对比
        boolean flag = orderInfoService.compareTradeNo(userId, tradeNoUI);
        if (!flag) {
            return RetVal.fail().message("不能重复提交订单！");
        }
        //验证库存与价格
        String warningMessage = orderInfoService.checkStockAndPrice(userId, orderInfo);
        if (!StringUtils.isEmpty(warningMessage)) {
            return RetVal.fail().message(warningMessage);
        }
        //保存订单的基本信息和详情信息
        Long orderId = orderInfoService.saveOrderAndDetail(orderInfo);
        //订单提交成功后还需要删除流水号
        orderInfoService.deleteTradeNo(userId);
        return RetVal.ok(orderId);
    }

    /**
     * 3.根据订单Id查询订单信息
     *
     * @param orderId
     * @return
     */
    @GetMapping("getOrderInfoById/{orderId}")
    public OrderInfo getOrderInfoById(@PathVariable Long orderId) {
        return orderInfoService.getById(orderId);
    }
}

