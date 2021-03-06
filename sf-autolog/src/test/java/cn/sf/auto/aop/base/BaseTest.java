package cn.sf.auto.aop.base;

import cn.sf.auto.aop.AspectJApplication;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = AspectJApplication.class)
@ActiveProfiles("default")
public class BaseTest {

}
