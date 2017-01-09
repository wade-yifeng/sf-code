package cn.sf.auto.log.clazz;

import cn.sf.auto.log.annotations.AutoExcp;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@AutoExcp(tag = "测试", module = "测试模块")
@Slf4j
@Component
public class ExcpClass {

    public Object test1() {
        log.info("ExcpClass.test1");
        throw new RuntimeException();
    }

}
