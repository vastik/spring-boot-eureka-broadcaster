package com.github.vastik.spring.eureka.broadcast;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@ComponentScan
public class EurekaBroadcastAutoConfiguration {

    @Bean("eurekaTaskExecutor")
    public ThreadPoolTaskExecutor eurekaTaskExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setThreadGroupName("eureka-pool");
        taskExecutor.setThreadNamePrefix("eureka-pool-");
        taskExecutor.setBeanName("eurekaTaskExecutor");
        taskExecutor.setCorePoolSize(5);
        taskExecutor.initialize();
        return taskExecutor;
    }
}
