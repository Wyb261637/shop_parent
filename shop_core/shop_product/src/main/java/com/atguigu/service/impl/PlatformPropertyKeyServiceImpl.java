package com.atguigu.service.impl;

import com.atguigu.entity.PlatformPropertyKey;
import com.atguigu.entity.PlatformPropertyValue;
import com.atguigu.mapper.PlatformPropertyKeyMapper;
import com.atguigu.service.PlatformPropertyKeyService;
import com.atguigu.service.PlatformPropertyValueService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * <p>
 * 属性表 服务实现类
 * </p>
 *
 * @author WangYiBing
 * @since 2022-07-20
 */
@Service
public class PlatformPropertyKeyServiceImpl extends ServiceImpl<PlatformPropertyKeyMapper, PlatformPropertyKey> implements PlatformPropertyKeyService {

    @Autowired
    private PlatformPropertyValueService propertyValueService;


//    @Override
//    //第一种写法 性能不高
//    public List<PlatformPropertyKey> getPlatformPropertyByCategoryId(Long category1Id, Long category2Id, Long category3Id) {
//        //a.根据分类id查询平台属性名称
//        List<PlatformPropertyKey> platformPropertyKeyList= baseMapper.getPlatformPropertyByCategoryId(category1Id,category2Id,category3Id);
//        //b.根据平台属性名称id查询属性
//        for (PlatformPropertyKey platformPropertyKey : platformPropertyKeyList) {
//            QueryWrapper<PlatformPropertyValue> wrapper = new QueryWrapper<>();
//            wrapper.eq("property_key_id",platformPropertyKey.getId());
//            List<PlatformPropertyValue> propertyValueList = propertyValueService.list(wrapper);
//            platformPropertyKey.setPropertyValueList(propertyValueList);
//        }
//        return platformPropertyKeyList;
//    }


    @Override
    //第二种写法 只用一条SQL语句就可以实现
    public List<PlatformPropertyKey> getPlatformPropertyByCategoryId(Long category1Id, Long category2Id, Long category3Id) {
        return baseMapper.getPlatformPropertyByCategoryId(category1Id,category2Id,category3Id);
    }

    /**
     * 保存平台属性
     * 1.判断是添加还是修改
     * 2.如果是修改 直接删除之前该平台属性key所有的平台属性值
     *
     * @param platformPropertyKey
     * @return
     */
    @Override
    public boolean savePlatformProperty(PlatformPropertyKey platformPropertyKey) {
        //判断是添加还是修改
        if(platformPropertyKey.getId()!=null){
            baseMapper.updateById(platformPropertyKey);
            //如果是修改 直接删除之前该平台属性key所有的平台属性值
            QueryWrapper<PlatformPropertyValue> wrapper = new QueryWrapper<>();
            wrapper.eq("property_key_id", platformPropertyKey.getId());
            propertyValueService.remove(wrapper);
        }else{
            //a.保存平台属性key
            baseMapper.insert(platformPropertyKey);
        }
        //b.保存平台属性value
        List<PlatformPropertyValue> propertyValueList = platformPropertyKey.getPropertyValueList();
        //判断平台属性list是否为空
        if(!CollectionUtils.isEmpty(propertyValueList)){
            for (PlatformPropertyValue propertyValue : propertyValueList) {
                propertyValue.setPropertyKeyId(platformPropertyKey.getId());
            }
            propertyValueService.saveBatch(propertyValueList);
        }
        return true;
    }

    @Override
    public List<PlatformPropertyKey> getPlatformPropertyBySkuId(Long skuId) {
        return baseMapper.getPlatformPropertyBySkuId(skuId);
    }
}
