package com.qinh.collectiontype;

/**
 * 课程类
 *
 * @author Qh
 * @version 1.0
 * @date 2021-04-13-23:04
 */
public class Course {

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Course{" +
                "name='" + name + '\'' +
                '}';
    }
}
