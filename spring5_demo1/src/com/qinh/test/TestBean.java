package com.qinh.test;

import com.qinh.bean.Emp;
import com.qinh.collectiontype.Book;
import com.qinh.collectiontype.Student;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author Qh
 * @version 1.0
 * @date 2021-04-12-20:58
 */
public class TestBean {

    //内部bean测试
    @Test
    public void testBean1(){

        //1.加载Spring配置文件
        //BeanFactory beanFactory = new ClassPathXmlApplicationContext("bean1.xml");
        ApplicationContext context = new ClassPathXmlApplicationContext("bean3.xml");

        //2.获取配置创建的对象
        Emp emp = context.getBean("emp", Emp.class);
        System.out.println(emp);
    }

    //级联赋值
    @Test
    public void testBean2(){

        //1.加载Spring配置文件
        //BeanFactory beanFactory = new ClassPathXmlApplicationContext("bean1.xml");
        ApplicationContext context = new ClassPathXmlApplicationContext("bean4.xml");

        //2.获取配置创建的对象
        Emp emp = context.getBean("emp", Emp.class);
        System.out.println(emp);
    }

    //数组、集合类型属性注入测试
    @Test
    public void testBean3(){

        //1.加载Spring配置文件
        //BeanFactory beanFactory = new ClassPathXmlApplicationContext("bean1.xml");
        ApplicationContext context = new ClassPathXmlApplicationContext("bean5.xml");

        //2.获取配置创建的对象
        Student student = context.getBean("student", Student.class);
        System.out.println(student);
    }

    //公共属性提取测试方法
    @Test
    public void testBean4(){

        //1.加载Spring配置文件
        //BeanFactory beanFactory = new ClassPathXmlApplicationContext("bean1.xml");
        ApplicationContext context = new ClassPathXmlApplicationContext("bean6.xml");

        //2.获取配置创建的对象
        Book book = context.getBean("book", Book.class);
        System.out.println(book);
    }

    @Test
    public void t1(){
        try {
            Class<?> clazz = Class.forName("com.qinh.collectiontype.Book");
            Book book = (Book)clazz.newInstance();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
    }


}
