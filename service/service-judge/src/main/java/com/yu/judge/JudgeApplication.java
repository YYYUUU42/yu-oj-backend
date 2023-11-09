package com.yu.judge;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;

import static com.yu.judge.mq.CodeMqInitMain.doInitCodeMq;

@SpringBootApplication
@MapperScan("com.yu.judge.mapper")
@EnableScheduling
@EnableAspectJAutoProxy(proxyTargetClass = true, exposeProxy = true)
@ComponentScan(basePackages = {"com.yu.judge","com.yu.common"})
@EnableDiscoveryClient
@EnableFeignClients(basePackages = {"com.yu.feign"})
public class JudgeApplication {
    public static void main(String[] args) {
        // 初始化消息队列
        doInitCodeMq();
        SpringApplication.run(JudgeApplication.class, args);
    }
}