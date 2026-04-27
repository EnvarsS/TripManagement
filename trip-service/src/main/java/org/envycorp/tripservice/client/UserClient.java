package org.envycorp.tripservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

// Звертаємося до REST API сервісу User
@FeignClient(name = "user-service", url = "${app.services.user-url:http://user-service:8081}")
public interface UserClient {
    @GetMapping("/api/users/{id}/exists")
    Boolean checkUserExists(@PathVariable("id") Long id);
}