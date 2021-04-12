package com.qinh.dao;

/**
 * @author Qh
 * @version 1.0
 * @date 2021-04-12-23:43
 */
public class UserDaoImpl implements UserDao {
    @Override
    public void update() {
        System.out.println("dao update......");
    }

    public void print(){
        System.out.println("打印.....");
    }
}
