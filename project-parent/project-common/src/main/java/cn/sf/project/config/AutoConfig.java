package cn.sf.project.config;

import cn.sf.project.aop.AutoExcpAop;
import cn.sf.project.aop.AutoLogAop;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AutoConfig {

    @Bean
    public AutoLogAop setupAutoLogAop() {
        return new AutoLogAop();
    }

    @Bean
    public AutoExcpAop setupAutoExcpAop() {
        return new AutoExcpAop();
    }
}
