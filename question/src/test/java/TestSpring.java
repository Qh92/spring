import com.qinh.dependence.constructor.A;
import com.qinh.dependence.constructor.B;
import com.qinh.dependence.constructor.C;
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

        //2.获取配置创建的对象 报如下异常
        //Cannot resolve reference to bean 'a' while setting constructor argument;
        // nested exception is org.springframework.beans.factory.BeanCurrentlyInCreationException: Error creating bean with name 'a':
        // Requested bean is currently in creation: Is there an unresolvable circular reference?
        A a = context.getBean("a", A.class);
        B b = context.getBean("b", B.class);
        C c = context.getBean("c", C.class);
    }
}
