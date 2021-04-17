package com.qinh.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * @Author Qh
 * @Date 2021/4/16 19:39
 * @Version 1.0
 */
@Configuration
@ComponentScan(basePackages = "com.qinh")
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class SpringConfig {
}
