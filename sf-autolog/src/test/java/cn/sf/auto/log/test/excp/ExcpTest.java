package cn.sf.auto.log.test.excp;

import cn.sf.auto.log.base.BaseTest;
import cn.sf.auto.log.clazz.ExcpClass;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class ExcpTest extends BaseTest {

    @Autowired(required = false)
    private ExcpClass excpClass;
    @Test
    public void test1(){
        System.out.println(excpClass.test1());
    }
}