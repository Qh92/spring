import com.qinh.service.UserService;
import org.junit.Test;
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
    }

}
