package com.qinh.test;

import com.qinh.aspect.annotation.User;
import com.qinh.config.SpringConfig;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @Author Qh
 * @Date 2021/4/16 20:12
 * @Version 1.0
 */
public class TestAop {

    @Test
    public void t1(){
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(SpringConfig.class);
        User user = context.getBean("user", User.class);
        user.add();
    }
}
