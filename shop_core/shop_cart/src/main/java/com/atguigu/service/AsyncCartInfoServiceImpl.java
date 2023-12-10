package com.atguigu.service;

import com.atguigu.entity.CartInfo;
import com.atguigu.mapper.CartInfoMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * projectName: shop_parent
 *
 * @author: WangYiBing
 * time: 2023/12/10 11:37 周日
 * description:
 */
@Async
@Service
public class AsyncCartInfoServiceImpl extends ServiceImpl<CartInfoMapper, CartInfo> implements AsyncCartInfoService {


    @Override
    public void saveCartInfo(CartInfo existCartInfo) {
        baseMapper.insert(existCartInfo);
    }
}
