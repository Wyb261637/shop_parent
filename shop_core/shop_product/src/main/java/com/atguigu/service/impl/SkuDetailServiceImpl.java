package com.atguigu.service.impl;

import com.atguigu.constant.RedisConst;
import com.atguigu.entity.ProductSalePropertyKey;
import com.atguigu.entity.SkuImage;
import com.atguigu.entity.SkuInfo;
import com.atguigu.exception.SleepUtils;
import com.atguigu.mapper.ProductSalePropertyKeyMapper;
import com.atguigu.service.SkuDetailService;
import com.atguigu.service.SkuImageService;
import com.atguigu.service.SkuInfoService;
import com.atguigu.service.SkuSalePropertyValueService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * projectName: shop_parent
 *
 * @author: WangYiBing
 * time: 2023/1/13 11:53 周五
 * description:
 */
@Service
public class SkuDetailServiceImpl implements SkuDetailService {

    @Autowired
    private SkuInfoService skuInfoService;

    @Autowired
    private SkuImageService skuImageService;

    @Autowired
    private SkuSalePropertyValueService skuSalePropertyValueService;

    @Resource
    private ProductSalePropertyKeyMapper salePropertyKeyMapper;

    @Resource
    private RedisTemplate redisTemplate;

    /**
     * 根据skuId查询商品的基本信息
     * 1.查询商品基本信息
     * 2.查询商品图片信息
     * 3.详情页面访问量过大，需要添加到redis中
     * @param skuId
     * @return
     */
    @Override
    public SkuInfo getSkuInfo(Long skuId) {
//        SkuInfo skuInfo = getSkuInfoFromDb(skuId);
//        SkuInfo skuInfo = getSkuInfoFromRedis(skuId);
        SkuInfo skuInfo = getSkuInfoFromRedisWithThreadLocal(skuId);

        return skuInfo;
    }

    /**
     * 利用redis+lua+threadLocal查询商品基本信息
     */
    public ThreadLocal<String> threadLocal = new ThreadLocal<>();
    private SkuInfo getSkuInfoFromRedisWithThreadLocal(Long skuId) {
        String cacheKey= RedisConst.SKUKEY_PREFIX+skuId+RedisConst.SKUKEY_SUFFIX;
        //a.从缓存中查询数据
        SkuInfo skuInfoRedis = (SkuInfo) redisTemplate.opsForValue().get(cacheKey);
        if(skuInfoRedis==null){
            //还有很多操作要执行 去查缓存 去判断 去过滤等操作 200行代码要执行
            String token = threadLocal.get();
            boolean accquireLock = false;
            //定义一个锁的名称 减小锁的粒度 让每一次访问都拿自己的锁
            String lockKey="lock-"+skuId;
            //代表第一次来 不参与自旋
            if (token == null) {
                token = UUID.randomUUID().toString();
                accquireLock = redisTemplate.opsForValue().setIfAbsent(lockKey, token, 30, TimeUnit.MINUTES);
            } else {
                //代表已经拿到锁了
                accquireLock = true;
            }
            if (accquireLock) {
                SkuInfo skuInfoDB = doBusiness(skuId, cacheKey);
                String luaScript = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
                DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
                redisScript.setScriptText(luaScript);
                redisScript.setResultType(Long.class);
                redisTemplate.execute(redisScript, Arrays.asList(lockKey), token);
                //擦屁股 防止内存泄漏
                threadLocal.remove();
                return skuInfoDB;
            } else {
                //自旋 只做一件事情 拿锁
                while (true) {
                    SleepUtils.millis(50);
                    //重试拿锁
                    boolean reTryAcquireLock = redisTemplate.opsForValue().setIfAbsent(lockKey, token, 30, TimeUnit.MINUTES);
                    if (reTryAcquireLock) {
                        //拿到锁以后就不要去自旋了
                        threadLocal.set(token);
                        break;
                    }
                }
                return getSkuInfoFromRedisWithThreadLocal(skuId);
            }
        }else{
            return skuInfoRedis;
        }
    }
    private SkuInfo doBusiness(Long skuId, String cacheKey) {
        //b.如果缓存里面没有数据 从数据库中查
        SkuInfo skuInfoDb = getSkuInfoFromDb(skuId);
        //c.放入数据到缓存
        redisTemplate.opsForValue().set(cacheKey, skuInfoDb, RedisConst.SKUKEY_TIMEOUT, TimeUnit.SECONDS);
        return skuInfoDb;
    }
    /**
     * 从redis中查询数据
     * @param skuId
     * @return
     */
    private SkuInfo getSkuInfoFromRedis(Long skuId) {
        //需要先查询redis是否有数据，没有的话再查询数据库，然后把数据添加到redis中去
        //创建缓存名 sku:24:info
        String cacheKey = RedisConst.SKUKEY_PREFIX + skuId + RedisConst.SKUKEY_SUFFIX;
        //1.从缓存中查询数据
        SkuInfo skuInfoRedis = (SkuInfo) redisTemplate.opsForValue().get(cacheKey);
        //判断是否为空
        if (skuInfoRedis == null) {
            //2.查询完数据库后把数据放到redis
            SkuInfo skuInfoDb = getSkuInfoFromDb(skuId);
            redisTemplate.opsForValue().set(cacheKey, skuInfoDb,RedisConst.SKUKEY_TIMEOUT, TimeUnit.SECONDS);
            //缓存数据为空就返回数据库查询的数据
            return skuInfoDb;
        }
        return skuInfoRedis;
    }

    /**
     * 从数据库中查询数据
     * @param skuId
     * @return
     */
    private SkuInfo getSkuInfoFromDb(Long skuId) {
        //1.查询商品基本信息
        SkuInfo skuInfo = skuInfoService.getById(skuId);
        //2.查询商品图片信息
        if (skuInfo != null) {
            QueryWrapper<SkuImage> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("sku_id", skuId);
            List<SkuImage> skuImageList = skuImageService.list(queryWrapper);
            skuInfo.setSkuImageList(skuImageList);
        }
        return skuInfo;
    }

    /**
     * 销售属性id的组合与skuId的对应关系
     *
     * @param productId
     * @return
     */
    @Override
    public Map<Object, Object> getSalePropertyAndSkuIdMapping(Long productId) {
        Map<Object, Object> salePropertyAndSkuIdMap = new HashMap<>();
        List<Map> retListMap = skuSalePropertyValueService.getSalePropertyAndSkuIdMapping(productId);
        retListMap.forEach(retMap -> {
            salePropertyAndSkuIdMap.put(retMap.get("sale_property_value_id"), retMap.get("sku_id"));
        });
        return salePropertyAndSkuIdMap;
    }

    /**
     * 获取所有的销售属性(spu全份)和sku的销售属性(一份)
     *
     * @param productId
     * @param skuId
     * @return
     */
    @Override
    public List<ProductSalePropertyKey> getSpuSalePropertyAndSelected(Long productId, Long skuId) {
        return salePropertyKeyMapper.getSpuSalePropertyAndSelected(productId, skuId);
    }
}
