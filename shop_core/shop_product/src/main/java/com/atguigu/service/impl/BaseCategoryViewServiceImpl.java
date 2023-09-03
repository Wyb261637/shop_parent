package com.atguigu.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.entity.BaseCategoryView;
import com.atguigu.mapper.BaseCategoryViewMapper;
import com.atguigu.service.BaseCategoryViewService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * VIEW 服务实现类
 * </p>
 *
 * @author WangYiBing
 * @since 2023-01-13
 */
@Service
public class BaseCategoryViewServiceImpl extends ServiceImpl<BaseCategoryViewMapper, BaseCategoryView> implements BaseCategoryViewService {

    @Override
    public List<JSONObject> getIndexCategory() {
        //b.查询所有的分类信息
        List<BaseCategoryView> categoryViewList = baseMapper.selectList(null);
        //c.找到所有的一级分类
        Map<Long, List<BaseCategoryView>> category1Map = categoryViewList.stream().collect(Collectors.groupingBy(BaseCategoryView::getCategory1Id));
       //所有的一级分类JSON数据
        List<JSONObject> allCategoryJson = new ArrayList<>();
        Integer index = 0;
        for (Map.Entry<Long, List<BaseCategoryView>> category1Entry : category1Map.entrySet()) {
            Long category1Id = category1Entry.getKey();
            List<BaseCategoryView> category1List = category1Entry.getValue();
            //构造一个JSON格式的数据(一级分类)
            JSONObject categoryJson1 = new JSONObject();
            categoryJson1.put("index", ++index);
            categoryJson1.put("categoryId", category1Id);
            categoryJson1.put("categoryName", category1List.get(0).getCategory1Name());
            List<JSONObject> category1Children = new ArrayList<>();
            //d.找到所有的二级分类
            Map<Long, List<BaseCategoryView>> category2Map = category1List.stream().collect(Collectors.groupingBy(BaseCategoryView::getCategory2Id));
            category2Map.entrySet().forEach(category2Entry -> {
                Long category2Id = category2Entry.getKey();
                List<BaseCategoryView> category2List = category2Entry.getValue();
                //构造一个JSON格式的数据(二级分类)
                JSONObject categoryJson2 = new JSONObject();
                categoryJson2.put("categoryId", category2Id);
                categoryJson2.put("categoryName", category2List.get(0).getCategory2Name());
                List<JSONObject> category2Children = new ArrayList<>();
                //e.找到所有的三级分类
                Map<Long, List<BaseCategoryView>> category3Map = category2List.stream().collect(Collectors.groupingBy(BaseCategoryView::getCategory3Id));
                category3Map.entrySet().forEach(category3Entry -> {
                    Long category3Id = category3Entry.getKey();
                    List<BaseCategoryView> category3List = category3Entry.getValue();
                    //构造一个JSON格式的数据(三级分类)
                    JSONObject categoryJson3 = new JSONObject();
                    categoryJson3.put("categoryId", category3Id);
                    categoryJson3.put("categoryName", category3List.get(0).getCategory3Name());
                    //二级分类的子节点
                    category2Children.add(categoryJson3);
                });
                categoryJson2.put("categoryChild", category2Children);
                //一级分类的子节点
                category1Children.add(categoryJson2);
            });
            categoryJson1.put("categoryChild", category1Children);
            allCategoryJson.add(categoryJson1);
        }
        return allCategoryJson;
    }
}
