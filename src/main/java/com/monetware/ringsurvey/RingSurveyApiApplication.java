package com.monetware.ringsurvey;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import springfox.documentation.swagger2.annotations.EnableSwagger2;
import tk.mybatis.spring.annotation.MapperScan;

@EnableScheduling
@EnableSwagger2
@SpringBootApplication
@MapperScan("com.monetware.ringsurvey.business.dao")
public class RingSurveyApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(RingSurveyApiApplication.class, args);
    }

}
