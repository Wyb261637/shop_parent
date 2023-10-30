package com.atguigu.mapper;

import com.atguigu.search.Product;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * projectName: shop_parent
 *
 * @author: WangYiBing
 * time: 2023/9/5 0:01 周二
 * description:
 */
public interface ProductMapper extends ElasticsearchRepository<Product,Long> {

}
