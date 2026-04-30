package com.kafka.test.client;

import com.kafka.test.FeignConfig;
import com.kafka.test.dto.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

// Припускаємо, що сервіси працюють у Docker (наприклад, user-service)
@FeignClient(name = "user-service", url = "${SERVICES_USER_URL:localhost:8081}", configuration = FeignConfig.class)
@Service
public interface UserClient {
    @GetMapping("/users/{id}")
    UserDTO getUserById(@PathVariable("id") Long id);
}