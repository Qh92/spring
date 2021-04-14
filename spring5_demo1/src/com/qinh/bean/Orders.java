package com.qinh.bean;

/**
 * @Author Qh
 * @Date 2021/4/14 19:24
 * @Version 1.0
 */
public class Orders {

    private String oname;

    public Orders() {
        System.out.println("调用无参构造器方法.....");
    }

    public String getOname() {
        return oname;
    }

    public void setOname(String oname) {
        this.oname = oname;
        System.out.println("调用set方法进行属性赋值....");
    }

    public void initMethod(){
        System.out.println("初始化方法加载....");
    }

    public void destroyMethod(){
        System.out.println("销毁方法....");
    }
}
