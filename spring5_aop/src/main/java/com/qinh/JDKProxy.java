package com.qinh;

import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;

/**
 * @author Qh
 * @version 1.0
 * @date 2021-04-15-21:47
 */
public class JDKProxy {

    public static void main(String[] args) {
        Class[] interfaces = {UserDao.class};
        System.out.println(Arrays.toString(interfaces));
        System.out.println(Arrays.toString(UserDao.class.getInterfaces()));

//        MyInvacation myInvacation = new MyInvacation(new UserDaoImpl());
        //创建接口实现类代理对象
//        UserDao userDao = (UserDao)Proxy.newProxyInstance(UserDao.class.getClassLoader(), UserDaoImpl.class.getInterfaces(), myInvacation);
//        int add = userDao.add(1, 2);
//        System.out.println(add);

        UserDao userDao = (UserDao)Proxy.newProxyInstance(UserDao.class.getClassLoader(), interfaces, (proxy, method, params) -> {
            System.out.println("开始调用.....");
            Object invoke = method.invoke(new UserDaoImpl(), params);
            System.out.println("结束调用......");
            return invoke;
        });
        int add = userDao.add(1, 2);
        System.out.println(add);
    }

}

class MyInvacation implements InvocationHandler{

    private Object object;

    public MyInvacation(Object object) {
        this.object = object;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        System.out.println("开始调用.....");
        Object invoke = method.invoke(object, args);
        System.out.println("结束调用......");
        return invoke;
    }
}



