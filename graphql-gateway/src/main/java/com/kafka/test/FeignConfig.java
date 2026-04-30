package com.kafka.test;

import feign.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {
    @Bean
    Logger.Level feignLoggerLevel() {
        // Рівні логування:
        // NONE — без логів (дефолт)
        // BASIC — тільки метод, URL та статус відповіді
        // HEADERS — BASIC + заголовки
        // FULL — заголовки, тіло запиту та відповіді, метадані
        return Logger.Level.FULL;
    }
}