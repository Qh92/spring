package com.qinh.dependence.constructor;

/**
 * @Author Qh
 * @Date 2021/6/23 16:25
 * @Version 1.0
 */
public class B {

    private C c;

    public B() {
    }

    public B(C c) {
        this.c = c;
    }

    public void setC(C c) {
        this.c = c;
    }
}
