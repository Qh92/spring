package com.qinh.dao;

import org.springframework.stereotype.Repository;

/**
 * @Author Qh
 * @Date 2021/4/14 23:07
 * @Version 1.0
 */
@Repository
public class UserDaoImpl2 implements UserDao{
    @Override
    public void add() {
        System.out.println("UserDaoImpl2 add ....");
    }
}
