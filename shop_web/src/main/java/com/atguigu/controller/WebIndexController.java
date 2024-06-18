package com.atguigu.controller;

import com.atguigu.client.ProductFeignClient;
import com.atguigu.client.SearchFeignClient;
import com.atguigu.result.RetVal;
import com.atguigu.search.SearchParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * projectName: shop_parent
 *
 * @author: WangYiBing
 * time: 2023/1/13 11:09 周五
 * description: Web页面详情显示
 */
@Controller
public class WebIndexController {
    @Autowired
    private ProductFeignClient productFeignClient;

    @Autowired
    private SearchFeignClient searchFeignClient;

    /**
     * 支持两种跳转方式：<a href="http://item.gmall.com/,http://item.gmall.com/index.html">...</a>
     */
    @RequestMapping({"/", "index.html"})
    public String index(Model model) {
        RetVal retVal = productFeignClient.getIndexCategory();
        model.addAttribute("list", retVal.getData());
        return "index/index";
    }

    /**
     * 2.商品搜索功能
     */
    @GetMapping("search.html")
    public String searchProduct(SearchParam searchParam,Model model) {
        //通过远程RPC调用
        RetVal<Map> retVal = searchFeignClient.searchProduct(searchParam);
        //不要写成 model.addAttribute(),因为这里要加多个属性
        model.addAllAttributes(retVal.getData());
        //1.搜索路径上参数的回显 TODO
        String urlParam=pageUrlParam(searchParam);
        model.addAttribute("urlParam",urlParam);
        //2.页面回显品牌信息
        String brandParam = pageBrandParam(searchParam.getBrandName());
        model.addAttribute("brandNameParam",brandParam);
        //3.页面回显平台属性信息
        List<Map<String,String>> propsParamList =pagePlatformParam(searchParam.getProps());
        model.addAttribute("propsParamList",propsParamList);
        //4.页面排序信息回显
        Map<String,Object> orderMap=pageSortParam(searchParam.getOrder());
        model.addAttribute("orderMap",orderMap);


        return "search/index";
    }
    //&order=1:asc
    private Map<String, Object> pageSortParam(String order) {
        Map<String, Object> orderMap= new HashMap<>();
        if(!StringUtils.isEmpty(order)){
            String[] orderSplit = order.split(":");
            if(orderSplit.length==2){
                orderMap.put("type",orderSplit[0]);
                orderMap.put("sort",orderSplit[1]);
            }
        }else{
            //默认给一个排序
            orderMap.put("type",1);
            orderMap.put("sort","desc");
        }
        return orderMap;
    }

    //&props=4:骁龙888:CPU型号&props=5:5.0英寸以下:屏幕尺寸
    private List<Map<String, String>> pagePlatformParam(String[] props) {
        List<Map<String, String>> propList=new ArrayList<>();
        if(props!=null&&props.length>0){
            for (String prop : props) {
                //4:骁龙888:CPU型号
                String[] propSplit = prop.split(":");
                if(propSplit.length==3){
                    Map<String, String> propMap = new HashMap<>();
                    propMap.put("propertyKeyId",propSplit[0]);
                    propMap.put("propertyKey",propSplit[2]);
                    propMap.put("propertyValue",propSplit[1]);
                    propList.add(propMap);
                }
            }
        }
        return propList;
    }

    private String pageBrandParam(String brandName) {
        //&brandName=3:三星
        if(!StringUtils.isEmpty(brandName)){
            String[]  brandSplit= brandName.split(":");
            if(brandSplit.length==2){
                return "品牌:"+brandSplit[1];
            }
        }
        return null;
    }

    //?keyword=三星&brandName=3:三星&props=4:骁龙888:CPU型号&props=5:5.0英寸以下:屏幕尺寸&order=2:asc
    private String pageUrlParam(SearchParam searchParam) {
        StringBuilder urlParam = new StringBuilder();
        //判断是否有关键字
        if(!StringUtils.isEmpty(searchParam.getKeyword())){
            urlParam.append("keyword=").append(searchParam.getKeyword());
        }
        //判断是否有分类id
        if(!StringUtils.isEmpty(searchParam.getCategory1Id())){
            urlParam.append("category1Id=").append(searchParam.getCategory1Id());
        }
        if(!StringUtils.isEmpty(searchParam.getCategory2Id())){
            urlParam.append("category2Id=").append(searchParam.getCategory2Id());
        }
        if(!StringUtils.isEmpty(searchParam.getCategory3Id())){
            urlParam.append("category3Id=").append(searchParam.getCategory3Id());
        }
        //判断是否有品牌
        if(!StringUtils.isEmpty(searchParam.getBrandName())){
            if(urlParam.length()>0){
                //原有地址栏有参数 才继续往下拼接
                urlParam.append("&brandName=").append(searchParam.getBrandName());
            }
        }
        //判断是否有平台属性  &props=4:骁龙888:CPU型号&props=5:5.0英寸以下:屏幕尺寸
        if(!StringUtils.isEmpty(searchParam.getProps())){
            if(urlParam.length()>0){
                //原有地址栏有参数 才继续往下拼接
                for (String prop:searchParam.getProps()){
                    urlParam.append("&props=").append(prop);
                }
            }
        }
        return "search.html?"+urlParam.toString();
    }
}
