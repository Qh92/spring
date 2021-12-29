import com.qinh.config.SpringConfig;
import com.qinh.service.TransactionValidService;
import com.qinh.service.UserService;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

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

    @Test
    public void t3(){
        ApplicationContext context = new AnnotationConfigApplicationContext(SpringConfig.class);
        TransactionValidService service = context.getBean("transactionValidService", TransactionValidService.class);
        //service.update1();
        service.update2();
    }

}
