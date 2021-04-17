package com.qinh.aspect.annotation;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * @author Qh
 * @version 1.0
 * @date 2021-04-17-18:44
 */
@Component
@Aspect
@Order(1)
public class PersonProxy {

    //前置通知
    //@Before注解表示作为前置通知
    @Before(value = "execution(* com.qinh.aspect.annotation.User.add(..))")
    public void before(){
        System.out.println("PersonProxy before......");
    }
}
