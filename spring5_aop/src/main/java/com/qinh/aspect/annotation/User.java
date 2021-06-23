package com.qinh.aspect.annotation;

import org.springframework.stereotype.Component;

/**
 * 被增强类
 *
 * @Author Qh
 * @Date 2021/4/16 19:35
 * @Version 1.0
 */
@Component
public class User {

    public void add(){

        /*try {
            int i = 10 / 0;
        } catch (Exception e) {
            //e.printStackTrace();
            throw new RuntimeException("java.lang.ArithmeticException");
        }*/

        System.out.println("add ........");
        //int i = 10 / 0;
    }
}
