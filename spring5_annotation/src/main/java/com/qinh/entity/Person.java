package com.qinh.entity;

import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * @Author Qh
 * @Date 2021/4/23 16:44
 * @Version 1.0
 */
@Component
public class Person implements ApplicationListener {

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        System.out.println("触发监听");
    }
}
