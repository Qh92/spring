import com.alibaba.druid.pool.DruidDataSource;
import com.qinh.config.SpringConfig;
import com.qinh.service.UserService;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @Author Qh
 * @Date 2021/4/14 22:33
 * @Version 1.0
 */
public class TestSpring {


    @Test
    public void t1(){
        //1.加载Spring配置文件
        //BeanFactory beanFactory = new ClassPathXmlApplicationContext("bean1.xml");
        ApplicationContext context = new ClassPathXmlApplicationContext("spring.xml");

        //2.获取配置创建的对象
        UserService userService = context.getBean("userService", UserService.class);
        System.out.println("获取bean实例 ： " + userService);
        userService.add();
    }


    @Test
    public void t2(){
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(SpringConfig.class);

        //2.获取配置创建的对象
        UserService userService = context.getBean("userService", UserService.class);
        System.out.println("获取bean实例 ： " + userService);
        userService.add();
    }

}
