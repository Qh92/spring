package com.qinh.dao;

import org.springframework.stereotype.Repository;

/**
 * @Author Qh
 * @Date 2021/4/14 23:00
 * @Version 1.0
 */
@Repository
public class UserDaoImpl implements UserDao {
    @Override
    public void add() {
        System.out.println("UserDaoImpl add....");
    }

    public void test(){
        System.out.println("test ......");
    }
}
