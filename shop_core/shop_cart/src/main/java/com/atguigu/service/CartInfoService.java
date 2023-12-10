package com.atguigu.service;

import com.atguigu.entity.CartInfo;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * <p>
 * 购物车表 用户登录系统时更新冗余 服务类
 * </p>
 *
 * @author WangYiBing
 * @since 2023-11-21
 */
public interface CartInfoService extends IService<CartInfo> {

    /**
     * 1.加入购物车
     * @param oneOfUserId
     * @param skuId
     * @param skuNum
     */
    void addToCart(String oneOfUserId, Long skuId, Integer skuNum);

    /**
     * 2.购物车列表
     * @param request
     * @return
     */
    List<CartInfo> getCartList(HttpServletRequest request);

    /**
     * 3.购物车勾选
     * @param oneOfUserId
     * @param skuId
     * @param isChecked
     */
    void checkCart(String oneOfUserId, Long skuId, Integer isChecked);
    /**
     * 4.删除购物车
     * @param oneOfUserId
     * @param skuId
     * @return
     */
    void deleteCart(String oneOfUserId, Long skuId);
    /**
     * 5.查询购物车勾选信息,其他微服务调用的不需要使用 RetVal
     * @param userId
     * @return
     */
    List<CartInfo> getSelectCartInfo(String userId);
}
