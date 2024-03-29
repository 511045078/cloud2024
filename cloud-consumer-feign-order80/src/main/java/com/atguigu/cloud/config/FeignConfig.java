package com.atguigu.cloud.config;

import feign.Logger;
import feign.Retryer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {

    @Bean
    public Retryer myRetryer(){
//        return Retryer.NEVER_RETRY;
        return new Retryer.Default(100, 1, 3);
    }

//  feign的日志功能默认不开启，这里设置开启最高级别日志
    @Bean
    Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }
}
