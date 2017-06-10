package cn.sf.project;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ImportResource;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Created by nijianfeng on 17/6/10.
 */
@SpringBootApplication
@ImportResource(value = {
        "classpath:/spring/consumer.xml",
        "classpath:/spring/provider.xml"
})
@ComponentScan(basePackages = { "cn.sf.project"})
@EnableTransactionManagement
public class CenterApp {

    public static void main(String args[]) {
        SpringApplication.run(CenterApp.class, args);
    }

}
