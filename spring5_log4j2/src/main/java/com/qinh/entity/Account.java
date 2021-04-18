package com.qinh.entity;

/**
 * @author Qh
 * @version 1.0
 * @date 2021-04-18-0:47
 */
public class Account {

    private String id;

    private String username;

    private Integer money;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getMoney() {
        return money;
    }

    public void setMoney(Integer money) {
        this.money = money;
    }
}
