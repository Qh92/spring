package com.qinh.factorybean;

import com.qinh.collectiontype.Course;
import org.springframework.beans.factory.FactoryBean;

/**
 * @author Qh
 * @version 1.0
 * @date 2021-04-13-23:29
 */
public class MyBean implements FactoryBean<Course> {


    @Override
    public Course getObject() throws Exception {
        Course course = new Course();
        course.setName("算法导论");
        return course;
    }

    @Override
    public Class<?> getObjectType() {
        return null;
    }

    @Override
    public boolean isSingleton() {
        return false;
    }
}
