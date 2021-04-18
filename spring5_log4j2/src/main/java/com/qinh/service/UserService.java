package com.qinh.service;

import com.qinh.dao.UserDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Qh
 * @version 1.0
 * @date 2021-04-18-0:48
 */

@Service
/**
 * @Transactional，这个注解添加到类上面，也可以添加方法上面
 * 如果把这个注解添加类上面，这个类里面所有的方法都添加事务
 * 如果把这个注解添加方法上面，为这个方法添加事务
 * 1.propagation：事务传播行为
 * （1）多事务方法之间进行调用，这个过程中事务 是如何进行管理的
 * 2.isolation：事务隔离级别
 * （1）事务有特性成为隔离性，多事务操作之间不会产生影响。不考虑隔离性产生很多问题
 * （2）有三个读问题：脏读、不可重复读、虚（幻）读
 * （3）脏读：一个未提交事务读取到另一个未提交事务的数据
 * （4）不可重复读：一个未提交事务读取到另一提交事务修改数据
 * （5）虚读：一个未提交事务读取到另一提交事务添加数据
 * （6）解决：通过设置事务隔离级别，解决读问题
 * 3、timeout：超时时间
 * （1）事务需要在一定时间内进行提交，如果不提交进行回滚
 * （2）默认值是 -1 ，设置时间以秒单位进行计算
 * 4、readOnly：是否只读
 * （1）读：查询操作，写：添加修改删除操作
 * （2）readOnly 默认值 false，表示可以查询，可以添加修改删除操作
 * （3）设置 readOnly 值是 true，设置成 true 之后，只能查询
 * 5、rollbackFor：回滚
 * （1）设置出现哪些异常进行事务回滚
 * 6、noRollbackFor：不回滚
 * （1）设置出现哪些异常不进行事务回滚
 */
//@Transactional(propagation = Propagation.REQUIRED,timeout = -1,rollbackFor = Exception.class)
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserDao userDao;


    /**
     * 转账方法
     * MySQL默认隔离级别 Isolation.REPEATABLE_READ
     */
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void accountMoney(){
        //编程式事务管理
        /*try {
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
        }*/
        logger.info("开始调用.....");
        //mary少100
        userDao.reduceMeney();
        //模拟异常
        int i = 10 / 0;
        //lucy多100
        userDao.addMoney();
    }

    /**
     * 测试事务传播级别
     */
    //@Transactional
    public void testPropagation(){
        userDao.reduceMeney();
        addMoney();
    }

    //@Transactional(propagation = Propagation.REQUIRES_NEW)
    public void addMoney(){
        userDao.addMoney();
    }
}
