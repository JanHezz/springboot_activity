package com.jan.springboot_activity;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//一定排除掉不然启动回去加载

@EnableAutoConfiguration(exclude = {
        org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class
})
@SpringBootApplication()
@MapperScan(value = "com.jan.springboot_activity.mapper")
public class SpringbootActivityApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringbootActivityApplication.class, args);
    }

}
