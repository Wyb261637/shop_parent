package com.atguigu.collection;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * projectName: shop_parent
 *
 * @author: WangYiBing
 * time: 2023/9/3 12:09 周日
 * description:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Product {

    public int id;
    public int num;
    public int price;
    public String name;
    public String category;

//    public Product(int id, int num, int price, String name, String category) {
//        this.id = id;
//        this.num = num;
//        this.price = price;
//        this.name = name;
//        this.category = category;
//    }
}
