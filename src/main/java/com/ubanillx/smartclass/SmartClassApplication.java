package com.ubanillx.smartclass;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 应用程序入口
 */
@SpringBootApplication
@MapperScan("com.ubanillx.smartclass.mapper")
@ServletComponentScan
@EnableAspectJAutoProxy(proxyTargetClass = true, exposeProxy = true)
@EnableScheduling
@EnableAsync // 添加异步支持，配合事件监听器使用
public class SmartClassApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmartClassApplication.class, args);
    }
} 