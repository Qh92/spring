package com.qinh.service;

import com.qinh.dao.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * 事务失效测试：具体见《事务不生效.md》
 *
 * @author Qh
 * @version 1.0
 * @date 2021/12/29 15:16
 */
@Service
public class TransactionValidService {

    @Autowired
    private UserDao userDao;

    @Autowired(required = false)
    private TransactionTemplate transactionTemplate;

    @Transactional
    public void update0() {
        userDao.modifyAge();
        int i = 10 / 0;
    }

    /**
     * 1.异常被捕获住了
     * 这种情况小白是最容易犯错的，在整个事务的方法中使用try-catch，导致异常无法抛出，自然会导致事务失效
     * @Transactional
     * public void method(){
     *   try{
     *     //插入一条数据
     *     //更改一条数据
     *   }catch(Exception ex){
     *     return;
     *   }
     * }
     */
    @Transactional
    public void update1(){
        try {
            userDao.modifyAge();
            int i = 10 / 0;
        } catch (Exception e) {
            e.printStackTrace();
            //解决办法，将异常再抛出
            throw e;
        }
    }

    /**
     * 2.方法中调用同类的方法
     * 简单的说就是一个类中的update2方法（未标注声明式事务）在内部调用了update0方法(标注了声明式事务)，这样会导致update0方法中的事务失效
     */
    @Transactional
    public void update2(){
        update0();
    }


    /**
     * 在spring中为了支持编程式事务，专门提供了一个类：TransactionTemplate，在它的execute方法中，就实现了事务的功能
     */
    public void update3() {
        transactionTemplate.execute(status -> {
            //addData1();
            //updateData2();
            return Boolean.TRUE;
        });
    }
}
