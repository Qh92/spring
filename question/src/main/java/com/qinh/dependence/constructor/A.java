package com.qinh.dependence.constructor;

import org.springframework.stereotype.Component;

/**
 * 构造器循环依赖 A->B->C->A
 *
 * @Author Qh
 * @Date 2021/6/23 16:25
 * @Version 1.0
 */
@Component
public class A {

    private B b;

    public A() {
    }

    public A(B b) {
        this.b = b;
    }

    public void setB(B b) {
        this.b = b;
    }
}
