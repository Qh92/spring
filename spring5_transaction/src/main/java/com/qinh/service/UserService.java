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
        //lucy少100
        userDao.reduceMeney();
        //mary多100
        userDao.addMoney();
    }
}
