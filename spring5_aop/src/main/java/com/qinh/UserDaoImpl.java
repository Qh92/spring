package com.qinh;

/**
 * @author Qh
 * @version 1.0
 * @date 2021-04-15-21:46
 */
public class UserDaoImpl implements UserDao {
    @Override
    public int add(int a, int b) {
        System.out.println("调用add方法....");
        return a + b;
    }

    @Override
    public String update(String id) {
        return id;
    }
}
