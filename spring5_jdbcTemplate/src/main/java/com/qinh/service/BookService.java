package com.qinh.service;

import com.qinh.dao.BookDao;
import com.qinh.entity.Book;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Qh
 * @version 1.0
 * @date 2021-04-17-19:27
 */
@Service
public class BookService {

    @Autowired
    private BookDao bookDao;

    /**
     * 添加book
     *
     * @param book
     */
    public void addBook(Book book){
        bookDao.addBook(book);
    }

    public void updateBook(Book book){
        bookDao.updateBook(book);
    }

    public void deleteBook(Book book){
        bookDao.deleteBook(book);
    }

    /**
     * 查询表中记录数
     *
     * @return 表中记录数
     */
    public int queryCount(){
        return bookDao.selectCount();
    }

    /**
     * 查询单个对象
     *
     * @param id :Book id值
     * @return 查询的Book对象
     */
    public Book getBook(String id){
        return bookDao.getBook(id);
    }

    /**
     * 查询所有记录
     *
     * @return
     */
    public List<Book> listBook(){
        return bookDao.listBook();
    }


    /**
     * 批量添加数据
     *
     * @param batchArgs
     */
    public void batchAdd(List<Object[]> batchArgs){
        bookDao.batchAdd(batchArgs);
    }

    /**
     * 批量修改数据
     *
     * @param batchArgs
     */
    public void batchUpdate(List<Object[]> batchArgs){
        bookDao.batchUpdate(batchArgs);
    }


    /**
     * 批量删除数据
     *
     * @param batchArgs
     */
    public void batchDelete(List<Object[]> batchArgs){
        bookDao.batchDelete(batchArgs);
    }
}
