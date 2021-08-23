package com.qinh.context;

import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * 自定义的ApplicationContext,用于设置初始环境属性和修改某些成员变量值
 *
 * @author Qh
 * @version 1.0
 * @date 2021-08-21-12:56
 */
public class MyClassPathXmlApplicationContext extends ClassPathXmlApplicationContext {

    public MyClassPathXmlApplicationContext(String... configLocations){
        super(configLocations);
    }

    /**
     * 设置必要的属性，后续会检验该环境变量是否存在，如果不存在则系统运行报错
     */
    @Override
    protected void initPropertySources() {
        System.out.println("设置必要的环境变量");
        getEnvironment().setRequiredProperties("username");
    }

    /**
     * 此方法是用来实现BeanFactory的属性设置，主要是设置两个属性:
     * allowBeanDefinitionOverriding:是否允许覆盖同名称的不同定义的对象
     * allowCircularReferences:是否允许bean之间的循环依赖
     *
     * @param beanFactory beanFactory工厂
     */
    @Override
    protected void customizeBeanFactory(DefaultListableBeanFactory beanFactory) {
        super.setAllowBeanDefinitionOverriding(false);
        super.setAllowCircularReferences(false);
    }
}
