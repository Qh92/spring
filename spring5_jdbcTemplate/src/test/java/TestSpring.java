import com.qinh.entity.Book;
import com.qinh.service.BookService;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Qh
 * @version 1.0
 * @date 2021-04-17-19:57
 */
public class TestSpring {

    @Test
    public void t1(){
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("spring.xml");
        BookService bookService = context.getBean("bookService", BookService.class);
        Book book = new Book();
        book.setId("2");
        book.setName("Python编程");
        book.setPrice(new BigDecimal(299.99));
        bookService.addBook(book);

        /*Book book2 = new Book();
        book2.setId("1");
        book2.setName("Java高并发编程");
        book2.setPrice(new BigDecimal(199.99));
        bookService.updateBook(book2);*/

        /*Book book3 = new Book();
        book3.setId("2");
        book3.setName("Python编程");
        book3.setPrice(new BigDecimal(299.99));
        bookService.deleteBook(book3);*/

    }

    @Test
    public void t2(){
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("spring.xml");
        BookService bookService = context.getBean("bookService", BookService.class);

        //查询单个数据
        int count = bookService.queryCount();
        System.out.println(count);

        System.out.println();

        //查询单个对象
        Book book = bookService.getBook("1");
        System.out.println(book);

        System.out.println();

        //查询对象集合
        List<Book> books = bookService.listBook();
        System.out.println(books);
    }

    //批量操作
    @Test
    public void t3(){
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("spring.xml");
        BookService bookService = context.getBean("bookService", BookService.class);

        //批量添加
        /*List<Object[]> parms = new ArrayList<>();
        Object[] o1 = {"3","算法导论",new BigDecimal(399.99)};
        Object[] o2 = {"4","Linux",new BigDecimal(79.99)};
        Object[] o3 = {"5","MySQL",new BigDecimal(69.99)};
        parms.add(o1);
        parms.add(o2);
        parms.add(o3);
        bookService.batchAdd(parms);*/

        //批量修改
        /*List<Object[]> parms = new ArrayList<>();
        Object[] o1 = {"算法导论1",new BigDecimal(299.99),"3"};
        Object[] o2 = {"Linux1",new BigDecimal(89.99),"4"};
        Object[] o3 = {"MySQL1",new BigDecimal(69.99),"5"};
        parms.add(o1);
        parms.add(o2);
        parms.add(o3);
        bookService.batchUpdate(parms);*/

        //批量删除
        List<Object[]> parms = new ArrayList<>();
        Object[] o1 = {"3"};
        Object[] o2 = {"4"};
        Object[] o3 = {"5"};
        parms.add(o1);
        parms.add(o2);
        parms.add(o3);
        bookService.batchDelete(parms);

    }
}
