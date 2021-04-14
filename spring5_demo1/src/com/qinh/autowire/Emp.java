package com.qinh.autowire;

/**
 * 员工类
 *
 * @author Qh
 * @version 1.0
 * @date 2021-04-13-22:08
 */
public class Emp {

    private String ename;
    private String gender;

    /**
     * 员工属于某一个部门
     */
    private Dept dept;

    public String getEname() {
        return ename;
    }

    public void setEname(String ename) {
        this.ename = ename;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Dept getDept() {
        return dept;
    }

    public void setDept(Dept dept) {
        this.dept = dept;
    }

    @Override
    public String toString() {
        return "Emp{" +
                "ename='" + ename + '\'' +
                ", gender='" + gender + '\'' +
                ", dept=" + dept +
                '}';
    }

    public void test(){
        System.out.println(dept);
    }
}
