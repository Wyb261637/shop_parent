package com.atguigu;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import com.baomidou.mybatisplus.generator.config.GlobalConfig;
import com.baomidou.mybatisplus.generator.config.PackageConfig;
import com.baomidou.mybatisplus.generator.config.StrategyConfig;
import com.baomidou.mybatisplus.generator.config.rules.DateType;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;

/**
 * projectName: shop_parent
 *
 * @author: WangYiBing
 * time: 2022/12/27 11:10 周二
 * description: mybatis逆向工程
 */
public class CodeGenerator {
    public static void main(String[] args) {
        // 1、创建代码生成器
        AutoGenerator autoGenerator = new AutoGenerator();
        // 2、全局配置
        GlobalConfig globalConfig = new GlobalConfig();
        //String projectPath = System.getProperty("user.dir");
        //globalConfig.setOutputDir(projectPath + "/src/main/java");

        globalConfig.setOutputDir("F:\\Study\\Java\\shop_parent\\shop_core\\shop_order\\src\\main\\java");
        globalConfig.setAuthor("WangYiBing");
        //生成后是否打开资源管理器
        globalConfig.setOpen(false);
        //重新生成时文件是否覆盖
        globalConfig.setFileOverride(false);
        //去掉Service接口的首字母I
        globalConfig.setServiceName("%sService");
        //主键策略
        globalConfig.setIdType(IdType.ID_WORKER_STR);
        //定义生成的实体类中日期类型
        globalConfig.setDateType(DateType.ONLY_DATE);
        //开启Swagger2模式
        globalConfig.setSwagger2(true);
        autoGenerator.setGlobalConfig(globalConfig);

        // 3、数据源配置
        DataSourceConfig dataSourceConfig = new DataSourceConfig();
        dataSourceConfig.setUrl("jdbc:mysql://192.168.2.129:3306/shop_order?serverTimezone=GMT%2B8");
        dataSourceConfig.setDriverName("com.mysql.jdbc.Driver");
        dataSourceConfig.setUsername("root");
        dataSourceConfig.setPassword("root");
        dataSourceConfig.setDbType(DbType.MYSQL);
        autoGenerator.setDataSource(dataSourceConfig);

        // 4、包配置
        PackageConfig packageConfig = new PackageConfig();
        //模块名
        packageConfig.setModuleName("");
        packageConfig.setParent("com.atguigu");
        packageConfig.setController("controller");
        packageConfig.setEntity("entity");
        packageConfig.setService("service");
        packageConfig.setMapper("mapper");
        autoGenerator.setPackageInfo(packageConfig);

        // 5、策略配置
        StrategyConfig strategy = new StrategyConfig();
//        strategy.setInclude("product_sale_property_key","product_sale_property_value");
//        strategy.setInclude("base_brand");
//        strategy.setInclude("base_category1","base_category2","base_category3","platform_property_key","platform_property_value");
//        strategy.setInclude("base_category1","base_category2","base_category3");
        //strategy.setInclude("platform_property_name","platform_property_value","base_brand");
//        strategy.setInclude("product_spu","spu_sale_property_name","spu_sale_property_value","product_image");
//        strategy.setInclude("base_sale_property");
//        strategy.setInclude("sku_info","sku_platform_property_value","sku_sale_property_value","sku_image");
//        strategy.setInclude("base_category_view");
//        strategy.setInclude("user_info","user_address");
//        strategy.setInclude("cart_info");
//        strategy.setInclude("order_detail","order_info");
//        strategy.setInclude("seckill_product");
        //数据库表映射到实体的命名策略
        strategy.setNaming(NamingStrategy.underline_to_camel);
        //生成实体时去掉表前缀
        strategy.setTablePrefix(packageConfig.getModuleName() + "_");
        //数据库表字段映射到实体的命名策略
        strategy.setColumnNaming(NamingStrategy.underline_to_camel);
        // lombok 模型 @Accessors(chain = true) setter链式操作
        strategy.setEntityLombokModel(true);
        //restful api风格控制器
        strategy.setRestControllerStyle(true);
        //url中驼峰转连字符
        strategy.setControllerMappingHyphenStyle(true);
        autoGenerator.setStrategy(strategy);
        // 6、执行
        autoGenerator.execute();
    }
}
