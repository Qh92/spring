package com.qinh.test;

import com.qinh.Book;
import com.qinh.Orders;
import com.qinh.User;
import com.qinh.service.UserService;
import org.junit.Test;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.ClassPathResource;

/**
 * @author Qh
 * @version 1.0
 * @date 2021-04-12-20:58
 */
public class TestSpring5 {

    @Test
    public void testAdd(){

        //1.加载Spring配置文件
        //BeanFactory beanFactory = new ClassPathXmlApplicationContext("bean1.xml");
        //解析xml的时候不会创建bean实例
        BeanFactory beanFactory = new XmlBeanFactory(new ClassPathResource("bean1.xml"));
        //解析xml的时候就会创建bean实例（除懒加载等不会创建对象）
        ApplicationContext context = new ClassPathXmlApplicationContext("bean1.xml");

        //2.获取配置创建的对象
        User user = context.getBean("user", User.class);
        System.out.println(user);
        user.add();
    }

    @Test
    public void testBook1(){
        ApplicationContext context = new ClassPathXmlApplicationContext("bean1.xml");
        Book book1 = context.getBean("book", Book.class);
        Book book2 = context.getBean("book", Book.class);
        //两次输出对象地址是相同的，默认为单实例对象
        //设置scope="prototype"后，两个地址不相同
        System.out.println(book1);
        System.out.println(book2);
    }

    @Test
    public void testOrders(){
        ApplicationContext context = new ClassPathXmlApplicationContext("bean1.xml");
        Orders orders = context.getBean("orders", Orders.class);
        System.out.println(orders);
    }

    @Test
    public void testUserService(){
        ApplicationContext context = new ClassPathXmlApplicationContext("bean2.xml");
        UserService userService = context.getBean("userService", UserService.class);
        userService.add();
    }
}
