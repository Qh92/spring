import com.qinh.aspect.xml.Book;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author Qh
 * @version 1.0
 * @date 2021-04-17-19:01
 */
public class TestSpring {
    @Test
    public void t1(){
        //1.加载Spring配置文件
        //BeanFactory beanFactory = new ClassPathXmlApplicationContext("bean1.xml");
        ApplicationContext context = new ClassPathXmlApplicationContext("bean.xml");

        //2.获取配置创建的对象
        Book book = context.getBean("book", Book.class);
        System.out.println("获取bean实例 ： " + book);
        book.buy();
    }
}
