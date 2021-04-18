import com.qinh.config.SpringConfig;
import com.qinh.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

/**
 * @author Qh
 * @version 1.0
 * @date 2021-04-18-23:14
 */
//@ExtendWith(SpringExtension.class)
//@ContextConfiguration(classes = SpringConfig.class)
@SpringJUnitConfig(classes = SpringConfig.class) //使用一个复合注解替代上面两个注解完成整合
public class JUnit5Test {

    @Autowired
    //private UserService userService;

    //@Test
    public void t1(){
        //userService.accountMoney();
    }
}
