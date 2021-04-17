package com.qinh.collectiontype;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Qh
 * @version 1.0
 * @date 2021-04-13-22:47
 */
public class Student {

    /**
     * 数组类型属性
     */
    private String[] courses;

    /**
     * List集合类型属性
     */
    private List<String> list;

    /**
     * Map集合类型属性
     */
    private Map<String,String> map;

    /**
     * Set集合类型属性
     */
    private Set<String> set;

    /**
     * 学生所学多门课程
     */
    private List<Course> courseList;

    public String[] getCourses() {
        return courses;
    }

    public void setCourses(String[] courses) {
        this.courses = courses;
    }

    public List<String> getList() {
        return list;
    }

    public void setList(List<String> list) {
        this.list = list;
    }

    public Map<String, String> getMap() {
        return map;
    }

    public void setMap(Map<String, String> map) {
        this.map = map;
    }

    public Set<String> getSet() {
        return set;
    }

    public void setSet(Set<String> set) {
        this.set = set;
    }

    public List<Course> getCourseList() {
        return courseList;
    }

    public void setCourseList(List<Course> courseList) {
        this.courseList = courseList;
    }

    @Override
    public String toString() {
        return "Student{" +
                "courses=" + Arrays.toString(courses) +
                ", list=" + list +
                ", map=" + map +
                ", set=" + set +
                ", courseList=" + courseList +
                '}';
    }
}
