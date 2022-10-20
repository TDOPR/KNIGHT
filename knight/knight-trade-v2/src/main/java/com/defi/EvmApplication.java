package com.defi;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableScheduling
@EnableTransactionManagement
@MapperScan("com.defi.mapper")
@SpringBootApplication(scanBasePackages = {"com.defi"})
public class EvmApplication {

    public static void main(String[] args) {
        SpringApplication.run(EvmApplication.class, args);
    }
}
