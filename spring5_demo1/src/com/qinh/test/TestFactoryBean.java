package com.qinh.test;

import com.qinh.collectiontype.Book;
import com.qinh.collectiontype.Course;
import com.qinh.factorybean.MyBean;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author Qh
 * @version 1.0
 * @date 2021-04-13-23:30
 */
public class TestFactoryBean {

    @Test
    public void t1(){
        //1.加载Spring配置文件
        //BeanFactory beanFactory = new ClassPathXmlApplicationContext("bean1.xml");
        ApplicationContext context = new ClassPathXmlApplicationContext("bean7.xml");

        //2.获取配置创建的对象
        Course myBean = context.getBean("myBean", Course.class);
        System.out.println(myBean);
    }
}
