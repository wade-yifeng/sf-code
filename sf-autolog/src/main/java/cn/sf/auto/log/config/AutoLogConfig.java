package cn.sf.auto.log.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AutoLogConfig {
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
