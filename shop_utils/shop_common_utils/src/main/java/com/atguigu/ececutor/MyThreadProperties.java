package com.atguigu.ececutor;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * projectName: shop_parent
 *
 * @author: WangYiBing
 * time: 2023/9/3 9:49 周日
 * description:
 */
@Data
@ConfigurationProperties(prefix = "thread.pool")
public class MyThreadProperties {

    public Integer corePoolSize = 16;
    public Integer maximumPoolSize = 32;
    public Long keepAliveTime = 50L;
    public Integer queueLength = 100;
}
