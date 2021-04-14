package com.qinh.service;

import com.qinh.dao.UserDao;
import com.qinh.dao.UserDaoImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @Author Qh
 * @Date 2021/4/14 22:29
 * @Version 1.0
 */
//注解value属性值可以省略不写，默认值是类名称，首字母小写
//@Service(value = "userService")
@Component
public class UserService {

    @Value(value = "abc")
    private String name;

    //根据类型注入
    /*@Autowired
    @Qualifier(value = "userDaoImpl2")
    private UserDao userDao;*/

    //@Resource  //根据类型注入
    @Resource(name = "userDaoImpl2")
    private UserDao userDao;

    public void add(){
        System.out.println("service add .....");
        userDao.add();
        System.out.println(name);
        /*UserDaoImpl u = (UserDaoImpl) userDao;
        u.test();*/
    }
}
