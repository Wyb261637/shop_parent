package com.atguigu.filter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * projectName: shop_parent
 *
 * @author: WangYiBing
 * time: 2023/11/19 11:20 周日
 * description: 全局拦截器
 */
@Component
public class AccessFilter implements GlobalFilter {
    @Value("${filter.whiteList}")
    public String filterWhiteList;
    /**
     *
     * @param exchange 是一个网络交换机，存储请求和响应消息，它是一个不可变的实例
     * @param chain 是一个网关过滤列表，它用于链式调用
     * @return
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();
        //1.对于请求某些资源（请求白名单）必须要先登录
        for (String filterWhite : filterWhiteList.split(",")) {
            if (path.indexOf(filterWhite)!=-1){
                 //让它跳转到登录页面
                ServerHttpResponse response = exchange.getResponse();
                response.setStatusCode(HttpStatus.SEE_OTHER);
                response.getHeaders().set(HttpHeaders.LOCATION,"http://passport.gmall.com/login.html?originalUrl="+request.getURI());
                return response.setComplete();
            }
        }
        //如果不想拦截，放开
        return chain.filter(exchange);
    }
}
