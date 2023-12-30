package com.atguigu.service.impl;

import com.atguigu.client.ProductFeignClient;
import com.atguigu.constant.RedisConst;
import com.atguigu.entity.CartInfo;
import com.atguigu.entity.SkuInfo;
import com.atguigu.mapper.CartInfoMapper;
import com.atguigu.service.AsyncCartInfoService;
import com.atguigu.service.CartInfoService;
import com.atguigu.util.AuthContextHolder;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * <p>
 * 购物车表 用户登录系统时更新冗余 服务实现类
 * </p>
 *
 * @author WangYiBing
 * @since 2023-11-21
 */
@Service
public class CartInfoServiceImpl extends ServiceImpl<CartInfoMapper, CartInfo> implements CartInfoService {

    @Autowired
    private ProductFeignClient productFeignClient;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private AsyncCartInfoService asyncCartInfoService;

    @Override
    public void addToCart(String oneOfUserId, Long skuId, Integer skuNum) {
        //a.查询数据库中是否存在该商品信息
        QueryWrapper<CartInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", oneOfUserId);
        wrapper.eq("sku_id", skuId);
        CartInfo existCartInfo = baseMapper.selectOne(wrapper);
        //b.如果存在
        if (existCartInfo != null) {
            //把原来的商品数量传递过来
            existCartInfo.setSkuNum(existCartInfo.getSkuNum() + skuNum);
            //把最新的商品价格拿到
            BigDecimal skuPrice = productFeignClient.getSkuPrice(skuId);
            existCartInfo.setRealTimePrice(skuPrice);
            //更新数据库
            baseMapper.updateById(existCartInfo);
        } else {
            //c.如果不存在，新增一条数据
            existCartInfo = new CartInfo();
            SkuInfo skuInfo = productFeignClient.getSkuInfo(skuId);
            existCartInfo.setUserId(oneOfUserId);
            existCartInfo.setSkuId(skuId);
            existCartInfo.setSkuNum(skuNum);
            existCartInfo.setCartPrice(skuInfo.getPrice());
            existCartInfo.setImgUrl(skuInfo.getSkuDefaultImg());
            existCartInfo.setSkuName(skuInfo.getSkuName());
            //默认设置勾选商品
            existCartInfo.setIsChecked(1);
            //把最新的商品价格拿到
            BigDecimal skuPrice = productFeignClient.getSkuPrice(skuId);
            existCartInfo.setRealTimePrice(skuPrice);
            baseMapper.insert(existCartInfo);
//            asyncCartInfoService.saveCartInfo(existCartInfo);
        }
        //d.往redis中存一份
        //user:3:cart
        String userCartKey = getUserCartKey(oneOfUserId);
        redisTemplate.boundHashOps(userCartKey).put(skuId.toString(), existCartInfo);
    }

    @Override
    public List<CartInfo> getCartList(HttpServletRequest request) {
        String userId = AuthContextHolder.getUserId(request);
        String userTempId = AuthContextHolder.getUserTempId(request);
        List<CartInfo> cartInfoList = new ArrayList<>();
        //1.未登录
        if (StringUtils.isEmpty(userId) && !StringUtils.isEmpty(userTempId)) {
            cartInfoList = queryFromDbToRedis(userTempId);
        }
        //2.已登录
        if (!StringUtils.isEmpty(userId)) {
            //查询未登录的购物车
            List<CartInfo> noLoginCartInfoList = queryFromDbToRedis(userTempId);
            if (!CollectionUtils.isEmpty(noLoginCartInfoList)) {
                //合并已登录和未登录的购物车
                mergeCartInfoList(userId, userTempId);
                //合并以后删除临时用户的购物项
                cartInfoList = deleteNoLoginDataAndReload(userId, userTempId);
            } else {
                cartInfoList = queryFromDbToRedis(userId);
            }
        }
        return cartInfoList;
    }

    @Override
    public void checkCart(String oneOfUserId, Long skuId, Integer isChecked) {
        //a.从redis中查询并修改
        String userCartKey = getUserCartKey(oneOfUserId);
        BoundHashOperations boundHashOps = redisTemplate.boundHashOps(userCartKey);
        //从hash中根据skuId拿到商品的购物车商品信息
        if (boundHashOps.hasKey(skuId.toString())) {
            CartInfo redisCartInfo = (CartInfo) boundHashOps.get(skuId.toString());
            redisCartInfo.setIsChecked(isChecked);
            //更新到redis中
            boundHashOps.put(skuId.toString(), redisCartInfo);
            //设置过期时间
            setCartKeyExpire(userCartKey);
        }
        //b.修改数据库的内容
        checkCartInfo(oneOfUserId, skuId, isChecked);
    }

    @Override
    public void deleteCart(String oneOfUserId, Long skuId) {
        //a.先删除redis中的内容
        String userCartKey = getUserCartKey(oneOfUserId);
        BoundHashOperations boundHashOps = redisTemplate.boundHashOps(userCartKey);
        //从hash中根据skuId拿到商品的购物车商品信息
        if (boundHashOps.hasKey(skuId.toString())) {
            boundHashOps.delete(skuId.toString());
        }
        //b.删除数据库的内容
        deleteCartInfo(oneOfUserId, skuId);
    }

    @Override
    public List<CartInfo> getSelectCartInfo(String userId) {
        String userCartKey = getUserCartKey(userId);
        List<CartInfo> redisCartInfoList = redisTemplate.opsForHash().values(userCartKey);
        List<CartInfo> selectedCartInfoList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(redisCartInfoList)) {
            for (CartInfo cartInfo : redisCartInfoList) {
                if (cartInfo.getIsChecked() == 1) {
                    selectedCartInfoList.add(cartInfo);
                }
            }
        }
        return selectedCartInfoList;
    }

    private void deleteCartInfo(String oneOfUserId, Long skuId) {
        QueryWrapper<CartInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", oneOfUserId);
        wrapper.eq("sku_id", skuId);
        baseMapper.delete(wrapper);
    }

    private void checkCartInfo(String oneOfUserId, Long skuId, Integer isChecked) {
        CartInfo cartInfo = new CartInfo();
        cartInfo.setIsChecked(isChecked);
        QueryWrapper<CartInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", oneOfUserId);
        wrapper.eq("sku_id", skuId);
        baseMapper.update(cartInfo, wrapper);
    }

    private List<CartInfo> deleteNoLoginDataAndReload(String userId, String userTempId) {
        //删除数据库的数据
        QueryWrapper<CartInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userTempId);
        baseMapper.delete(wrapper);
        //删除redis中的数据
        String userIdKey = getUserCartKey(userId);
        redisTemplate.delete(userIdKey);
        String userTempKey = getUserCartKey(userTempId);
        redisTemplate.delete(userTempKey);
        //重新加载数据到redis
        return queryFromDbToRedis(userId);
    }

    private void mergeCartInfoList(String userId, String userTempId) {
        //未登录的购物项
        List<CartInfo> noLoginCartInfoList = queryFromDbToRedis(userTempId);
        //已登录的购物项
        List<CartInfo> loginCartInfoList = queryFromDbToRedis(userId);
//        for (CartInfo noLoginCartInfo : noLoginCartInfoList) {
//            for (CartInfo loginCartInfo : loginCartInfoList) {
//                //把未登录和已登录的都添加到购物项
//                if (noLoginCartInfo.getSkuId().equals(loginCartInfo.getSkuId())){
//                    //把未登录和已登录的数量相加
//                    loginCartInfo.setSkuNum(noLoginCartInfo.getSkuNum()+loginCartInfo.getSkuNum());
//                    //当未登录的时候该商品未勾选，合并后要勾选
//                    if (noLoginCartInfo.getIsChecked()==0){
//                        loginCartInfo.setIsChecked(1);
//                    }
//                    //更新数据库
//                    baseMapper.updateById(loginCartInfo);
//                }else {
//                    //如果已登录没有该购物项，更改skuId
//                    noLoginCartInfo.setUserId(userId);
//                    baseMapper.updateById(noLoginCartInfo);
//                }
//            }
//        }
        //循环优化，使用map存储
        Map<Long, CartInfo> longCartInfoMap = loginCartInfoList.stream().collect(Collectors.toMap(CartInfo::getSkuId, cartInfo -> cartInfo));
        for (CartInfo noLoginCartInfo : noLoginCartInfoList) {
            //把未登录和已登录的都添加到购物项
            if (longCartInfoMap.containsKey(noLoginCartInfo.getSkuId())) {
                CartInfo loginCartInfo = longCartInfoMap.get(noLoginCartInfo.getSkuId());
                //把未登录和已登录的数量相加
                loginCartInfo.setSkuNum(noLoginCartInfo.getSkuNum() + loginCartInfo.getSkuNum());
                //当未登录的时候该商品未勾选，合并后要勾选
                if (noLoginCartInfo.getIsChecked() == 0) {
                    loginCartInfo.setIsChecked(1);
                }
                //更新数据库
                baseMapper.updateById(loginCartInfo);
            } else {
                //如果已登录没有该购物项，更改skuId
                noLoginCartInfo.setUserId(userId);
                baseMapper.updateById(noLoginCartInfo);
            }
        }
    }

    private List<CartInfo> queryFromDbToRedis(String oneOfUserId) {
        String userCartKey = getUserCartKey(oneOfUserId);
        List<CartInfo> cartInfoList = redisTemplate.boundHashOps(userCartKey).values();
        //如果缓存没有信息那么就查询数据库
        if (CollectionUtils.isEmpty(cartInfoList)) {
            QueryWrapper<CartInfo> wrapper = new QueryWrapper<>();
            wrapper.eq("user_id", oneOfUserId);
            cartInfoList = baseMapper.selectList(wrapper);
            //放入缓存
            Map<String, CartInfo> cartMap = new HashMap<>();
            for (CartInfo cartInfo : cartInfoList) {
                //第一种方法
                // redisTemplate.boundHashOps(userCartKey).put(cartInfo.getUserId().toString(), cartInfo);
                //第二种方法,全部遍历完以后再加到缓存，提升性能
                cartMap.put(cartInfo.getUserId().toString(), cartInfo);
            }
            redisTemplate.boundHashOps(userCartKey).putAll(cartMap);
            //设置redis购物车过期时间
            setCartKeyExpire(userCartKey);
        }
        return cartInfoList;
    }

    private void setCartKeyExpire(String userCartKey) {
        redisTemplate.expire(userCartKey, RedisConst.USER_CART_EXPIRE, TimeUnit.SECONDS);
    }

    private String getUserCartKey(String oneOfUserId) {
        String userCartKey = RedisConst.USER_KEY_PREFIX + oneOfUserId + RedisConst.USER_CART_KEY_SUFFIX;
        return userCartKey;
    }
}
