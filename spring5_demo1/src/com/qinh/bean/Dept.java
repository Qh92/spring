package com.qinh.bean;

/**
 * 部门类
 *
 * @author Qh
 * @version 1.0
 * @date 2021-04-13-22:07
 */
public class Dept {
    private String dname;

    public String getDname() {
        return dname;
    }

    public void setDname(String dname) {
        this.dname = dname;
    }

    @Override
    public String toString() {
        return "Dept{" +
                "dname='" + dname + '\'' +
                '}';
    }
}
