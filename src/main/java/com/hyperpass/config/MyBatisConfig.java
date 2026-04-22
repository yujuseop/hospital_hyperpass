package com.hyperpass.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("com.hyperpass.mapper")
public class MyBatisConfig {
}
