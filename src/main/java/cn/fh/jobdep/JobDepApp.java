package cn.fh.jobdep;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement(proxyTargetClass = true)
public class JobDepApp {
    public static void main(String[] args) {
        SpringApplication.run(JobDepApp.class, args);
    }
}
