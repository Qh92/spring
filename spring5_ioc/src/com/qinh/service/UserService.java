package com.qinh.service;

import com.qinh.dao.UserDao;
import com.qinh.dao.UserDaoImpl;

/**
 * @author Qh
 * @version 1.0
 * @date 2021-04-12-23:41
 */
public class UserService {

    /**
     * 创建UserDao类型属性，生成set方法
     */
    private UserDao userDao;

    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    public void add(){
        System.out.println("service add.....");

        //之前做法
//        UserDao userDao = new UserDaoImpl();
//        userDao.update();
        UserDaoImpl u = (UserDaoImpl)userDao;
        u.update();
        u.print();
    }

    @Override
    public String toString() {
        return "UserService{" +
                "userDao=" + userDao +
                '}';
    }
}
