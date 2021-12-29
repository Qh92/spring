package com.qinh.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

/**
 * @author Qh
 * @version 1.0
 * @date 2021-04-18-0:49
 */
@Repository
public class UserDaoImpl implements UserDao{

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 增加钱
     */
    @Override
    public void addMoney(){
        String sql = "update t_account set money = money + ? where username = ? ";
        int i = 10 / 0;
        jdbcTemplate.update(sql,100,"lucy");
    }

    /**
     * 减少钱
     */
    @Override
    public void reduceMeney() {
        String sql = "update t_account set money = money - ? where username = ? ";
        jdbcTemplate.update(sql,100,"mary");
    }

    @Override
    public void modifyAge() {
        String sql = "update test set age = 33 where id = ?";
        jdbcTemplate.update(sql, 1);
    }
}
