package com.yupaoBackend;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication
@MapperScan("com.yupaoBackend.mapper")
@EnableScheduling
public class YupaoApplication {
    public static void main(String[] args) {
        SpringApplication.run(YupaoApplication.class, args);
    }

}
