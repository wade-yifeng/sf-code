package cn.sf.auto.log.test.log;

import cn.sf.auto.log.base.BaseTest;
import cn.sf.auto.log.clazz.LogClass;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class LogTest extends BaseTest {

    @Autowired(required = false)
    private LogClass logClass;
    @Test
    public void test1(){
        logClass.test1("input");
    }
}