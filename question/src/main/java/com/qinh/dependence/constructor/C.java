package com.qinh.dependence.constructor;

/**
 * @Author Qh
 * @Date 2021/6/23 16:26
 * @Version 1.0
 */
public class C {

    private A a;

    public C() {
    }

    public C(A a) {
        this.a = a;
    }

    public void setA(A a) {
        this.a = a;
    }
}
