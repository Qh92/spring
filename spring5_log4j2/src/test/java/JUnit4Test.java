import com.qinh.config.SpringConfig;
import com.qinh.service.UserService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

//import org.junit.Test;
//import org.junit.runner.RunWith;

/**
 * @author Qh
 * @version 1.0
 * @date 2021-04-18-23:06
 */
//@RunWith(SpringJUnit4ClassRunner.class) //单元测试框架
@ContextConfiguration(classes = SpringConfig.class) //加载配置文件
public class JUnit4Test {

    @Autowired
    private UserService userService;

    @Test
    public void t1(){
        userService.accountMoney();
    }
}
