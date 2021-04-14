package com.qinh.test;

import com.alibaba.druid.pool.DruidDataSource;
import com.qinh.autowire.Emp;
import com.qinh.bean.Orders;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @Author Qh
 * @Date 2021/4/14 19:29
 * @Version 1.0
 */
public class TestBeanLifecycle {

    @Test
    public void t1(){
        //1.加载Spring配置文件
        //BeanFactory beanFactory = new ClassPathXmlApplicationContext("bean1.xml");
        ApplicationContext context = new ClassPathXmlApplicationContext("bean8.xml");

        //2.获取配置创建的对象
        Orders orders = context.getBean("orders", Orders.class);
        System.out.println("获取bean实例 ： " + orders);
        System.out.println(orders);

        ((ClassPathXmlApplicationContext) context).close();
        /*
        调用无参构造器方法.....
        调用set方法进行属性赋值....
        在初始化方法执行之前执行......
        初始化方法加载....
        在初始化方法执行之后执行......
        获取bean实例 ： com.qinh.bean.Orders@462d5aee
        com.qinh.bean.Orders@462d5aee
        销毁方法....
         */
    }

    /**
     * 测试自动装配
     */
    @Test
    public void t2(){
        //1.加载Spring配置文件
        //BeanFactory beanFactory = new ClassPathXmlApplicationContext("bean1.xml");
        ApplicationContext context = new ClassPathXmlApplicationContext("bean9.xml");

        //2.获取配置创建的对象
        Emp emp = context.getBean("emp", Emp.class);
        System.out.println("获取bean实例 ： " + emp);

    }

    @Test
    public void t3(){
        //1.加载Spring配置文件
        //BeanFactory beanFactory = new ClassPathXmlApplicationContext("bean1.xml");
        ApplicationContext context = new ClassPathXmlApplicationContext("bean10.xml");

        //2.获取配置创建的对象
        DruidDataSource dataSource = context.getBean("dataSource", DruidDataSource.class);
        System.out.println("获取bean实例 ： " + dataSource);
        System.out.println(dataSource.getUsername());

    }
}
