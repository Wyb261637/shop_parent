package com.atguigu.collection;


import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * projectName: shop_parent
 *
 * @author: WangYiBing
 * time: 2023/9/3 12:13 周日
 * description:
 */

public class CollectionDemo {

    public static void main(String[] args) {
        Product product1 = new Product(1, 1, 15, "面包", "零食");
        Product product2 = new Product(2, 3, 20, "饼干", "零食");
        Product product3 = new Product(3, 2, 30, "月饼", "零食");
        Product product4 = new Product(4, 3, 30, "青岛啤酒", "啤酒");
        Product product5 = new Product(5, 10, 30, "百威啤酒", "啤酒");
        ArrayList<Product> productList = Lists.newArrayList(product1, product2, product3, product4, product5);
        //对集合进行迭代(多种方式)
//        productList.forEach(product->{
//            System.out.println("方法一：lambda表达式………………………………………………………………………………………………");
//            System.out.println(product);
//        });
//        productList.stream().forEach(product -> {
//            System.out.println("方法二：stream流………………………………………………………………………………………………");
//            System.out.println(product);
//        });
        //对商品进行分类
//        Map<String, List<Product>> productMap = productList.stream().collect(Collectors.groupingBy(Product::getCategory));
//        //对productMap进行迭代，map集合无法使用foreach进行迭代，只能使用entrySet()
//        for (Map.Entry<String, List<Product>> productEntry : productMap.entrySet()) {
//            System.out.print(productEntry.getKey());
//            System.out.println(productEntry.getValue());
//        }
        //对字母进行大写置换
        ArrayList<String> characterList = Lists.newArrayList("a", "b", "c", "d");
        List<String> uplist = characterList.stream().map(character -> {
//            System.out.println(character.toUpperCase());
            return character.toUpperCase();
        }).collect(toList());
        System.out.println(uplist);
    }
}
