package com.qinh;

/**
 * 使用有参构造注入
 *
 * @author Qh
 * @version 1.0
 * @date 2021-04-12-22:40
 */
public class Orders {

    private String name;
    private String address;

    public Orders(String name, String address) {
        this.name = name;
        this.address = address;
    }

    @Override
    public String toString() {
        return "Orders{" +
                "name='" + name + '\'' +
                ", address='" + address + '\'' +
                '}';
    }
}
