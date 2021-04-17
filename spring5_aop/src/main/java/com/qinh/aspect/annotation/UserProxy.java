package com.qinh.aspect.annotation;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

/**
 * 增强类
 *
 * @Author Qh
 * @Date 2021/4/16 19:36
 * @Version 1.0
 */
@Component
@Aspect()
public class UserProxy {

    //前置通知
    //@Before注解表示作为前置通知
    @Before(value = "execution(* com.qinh.aspect.annotation.User.add(..))")
    public void before(){
        System.out.println("before......");
    }

    //最终通知
    @After(value = "execution(* com.qinh.aspect.annotation.User.add(..))")
    public void after(){
        System.out.println("after......");
    }

    //返回通知
    @AfterReturning(value = "execution(* com.qinh.aspect.annotation.User.add(..))")
    public void afterReturning(){
        System.out.println("afterReturning......");
    }

    @AfterThrowing(value = "execution(* com.qinh.aspect.annotation.User.add(..))")
    public void afterThrowing(){
        System.out.println("afterThrowing......");
    }

    @Around(value = "execution(* com.qinh.aspect.annotation.User.add(..))")
    public void around(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        System.out.println("环绕前执行......");
        proceedingJoinPoint.proceed();
        System.out.println("环绕后执行.......");
    }


}
