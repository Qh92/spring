package com.qinh;

/**
 * set方法进行注入属性
 *
 * @author Qh
 * @version 1.0
 * @date 2021-04-12-22:25
 */
public class Book {

    private String name;
    private String author;
    private String address;

    public Book() {
    }

    //有参构造器注入
    public Book(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    //set方法注入
    public void setName(String name) {
        this.name = name;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    /*@Override
    public String toString() {
        return "Book{" +
                "name='" + name + '\'' +
                ", author='" + author + '\'' +
                ", address='" + address + '\'' +
                '}';
    }*/

    public static void main(String[] args) {
        Book book = new Book();
        book.setName("Spring5");
    }
}
