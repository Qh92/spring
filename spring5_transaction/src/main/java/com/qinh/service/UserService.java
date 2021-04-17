package com.qinh.service;

import com.qinh.dao.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Qh
 * @version 1.0
 * @date 2021-04-18-0:48
 */
@Service
public class UserService {

    @Autowired
    private UserDao userDao;


    /**
     * 转账方法
     */
    public void accountMoney(){
        try {
            //第一步 开启事务

            //第二步 进行业务操作
            //mary少100
            userDao.reduceMeney();
            //模拟异常
            int i = 10 / 0;
            //lucy多100
            userDao.addMoney();
            //第三步 没有发送异常, 提交事务
        } catch (Exception e) {
            e.printStackTrace();
            //第三步 出现异常,事务回滚
        }
    }
}
