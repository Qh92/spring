package com.qinh.collectiontype;

import java.util.List;

/**
 * @author Qh
 * @version 1.0
 * @date 2021-04-13-23:13
 */
public class Book {

    private List<String> list;

    public List<String> getList() {
        return list;
    }

    public void setList(List<String> list) {
        this.list = list;
    }

    @Override
    public String toString() {
        return "Book{" +
                "list=" + list +
                '}';
    }
}
