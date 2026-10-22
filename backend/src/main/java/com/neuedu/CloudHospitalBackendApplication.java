package com.neuedu;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.neuedu.mapper")
public class CloudHospitalBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(CloudHospitalBackendApplication.class, args);
    }

}
