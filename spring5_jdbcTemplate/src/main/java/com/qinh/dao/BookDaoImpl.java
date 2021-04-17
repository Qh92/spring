package com.qinh.dao;

import com.qinh.entity.Book;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;

/**
 * @author Qh
 * @version 1.0
 * @date 2021-04-17-19:28
 */
@Repository
public class BookDaoImpl implements BookDao{

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void addBook(Book book) {
        //1.创建sql语句
        String sql = "insert into t_book(id,name,price) values(?,?,?)";
        //2.调用方法实现
        Object[] args = {book.getId(),book.getName(), book.getPrice()};
        int update = jdbcTemplate.update(sql,args);
        System.out.println(update);
    }

    @Override
    public void updateBook(Book book) {
        //1.创建sql语句
        String sql = "update t_book set name = ? , price = ? where id = ?";
        //2.调用方法实现
        Object[] args = {book.getName(), book.getPrice(),book.getId()};
        int update = jdbcTemplate.update(sql,args);
        System.out.println(update);
    }

    @Override
    public void deleteBook(Book book) {
        //1.创建sql语句
        String sql = "delete from t_book where id = ? ";
        //2.调用方法实现
        Object[] args = {book.getId()};
        int update = jdbcTemplate.update(sql,args);
        System.out.println(update);
    }

    @Override
    public int selectCount() {
        //1.创建sql语句
        String sql = "select count(*) from t_book";
        //2.调用方法实现
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class);
        return count;
    }

    @Override
    public Book getBook(String id) {
        //1.创建sql语句
        String sql = "select id,name,price from t_book where id = ? ";
        //2.调用方法实现
        Object[] args = {id};
        Book book = jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<Book>(Book.class), args);
        return book;
    }

    @Override
    public List<Book> listBook() {
        //1.创建sql语句
        String sql = "select id,name,price from t_book ";
        //2.调用方法实现
        List<Book> bookList = jdbcTemplate.query(sql, new BeanPropertyRowMapper<Book>(Book.class));
        return bookList;
    }

    @Override
    public void batchAdd(List<Object[]> batchArgs) {
        //1.创建sql语句
        String sql = "insert into t_book(id,name,price) values(?,?,?)";
        //2.调用方法实现
        int[] ints = jdbcTemplate.batchUpdate(sql, batchArgs);
        System.out.println(Arrays.toString(ints));
    }

    @Override
    public void batchUpdate(List<Object[]> batchArgs) {
        //1.创建sql语句
        String sql = "update t_book set name = ? , price = ? where id = ?";
        //2.调用方法实现
        int[] ints = jdbcTemplate.batchUpdate(sql, batchArgs);
        System.out.println(Arrays.toString(ints));
    }

    @Override
    public void batchDelete(List<Object[]> batchArgs) {
        //1.创建sql语句
        String sql = "delete from t_book where id = ?";
        //2.调用方法实现
        int[] ints = jdbcTemplate.batchUpdate(sql, batchArgs);
        System.out.println(Arrays.toString(ints));
    }
}
