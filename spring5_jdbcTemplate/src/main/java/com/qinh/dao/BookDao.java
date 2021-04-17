package com.qinh.dao;

import com.qinh.entity.Book;

import java.util.List;

/**
 * @author Qh
 * @version 1.0
 * @date 2021-04-17-19:28
 */
public interface BookDao {

    void addBook(Book book);

    void updateBook(Book book);

    void deleteBook(Book book);

    int selectCount();

    Book getBook(String id);

    List<Book> listBook();

    void batchAdd(List<Object[]> batchArgs);

    void batchUpdate(List<Object[]> batchArgs);

    void batchDelete(List<Object[]> batchArgs);
}
