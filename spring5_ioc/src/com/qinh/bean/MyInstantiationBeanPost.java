package com.qinh.bean;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor;

/**
 * 容器级生命周期接口，InstantiationAwareBeanPostProcessor、BeanPostProcessor
 *
 * @Author Qh
 * @Date 2021/6/28 14:32
 * @Version 1.0
 */
public class MyInstantiationBeanPost implements InstantiationAwareBeanPostProcessor {
    @Override
    public Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName) throws BeansException {
        System.out.println("调用MyInstantiationBeanPost的postProcessBeforeInstantiation方法");
        System.out.println("beanClass: " + beanClass);
        System.out.println("beanName: " + beanName);
        return null;
    }
}
