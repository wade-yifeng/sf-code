package cn.sf.auto.aop.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AutoConfig {
    @Bean
    public AutoLogAspectJ setupAutoLogAspectJ() {
        AutoLogAspectJ autoLogAspectJ = new AutoLogAspectJ();
        return autoLogAspectJ;
    }

    @Bean
    public AutoExcpAspectJ setupAutoExcpAspectJ() {
        AutoExcpAspectJ autoExcpAspectJ = new AutoExcpAspectJ();
        return autoExcpAspectJ;
    }
}
