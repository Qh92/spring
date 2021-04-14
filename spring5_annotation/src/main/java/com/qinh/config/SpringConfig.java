package com.qinh.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * 配置类。代替xml配置文件
 *
 * @Author Qh
 * @Date 2021/4/14 23:29
 * @Version 1.0
 */
@Configuration
@ComponentScan(basePackages = {"com.qinh"} )
public class SpringConfig {
}
