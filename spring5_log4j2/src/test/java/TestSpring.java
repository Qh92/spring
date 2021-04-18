import com.qinh.config.SpringConfig;
import com.qinh.entity.User;
import com.qinh.service.UserService;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.GenericApplicationContext;

/**
 * @author Qh
 * @version 1.0
 * @date 2021-04-17-19:57
 */
public class TestSpring {

    @Test
    public void t1(){
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("spring.xml");
        UserService service = context.getBean("userService", UserService.class);
        service.accountMoney();
        //service.testPropagation();
    }


    @Test
    public void t2(){
        ApplicationContext context = new AnnotationConfigApplicationContext(SpringConfig.class);
        UserService service = context.getBean("userService", UserService.class);
        service.accountMoney();
    }

    /**
     * 函数式风格创建对象，交给spring进行管理
     */
    @Test
    public void t3(){
        //1.创建GenericApplicationContext对象
        GenericApplicationContext context = new GenericApplicationContext();
        //2.调用context的方法对象注册
        context.refresh();
        context.registerBean("user",User.class,() -> new User());
        //3.获取在spring注册的对象
        User user = (User) context.getBean("user");
        System.out.println(user);
    }

}
