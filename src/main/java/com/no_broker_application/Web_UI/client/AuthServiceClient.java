package com.no_broker_application.Web_UI.client;

import com.no_broker_application.Web_UI.dto.AuthRequest;
import com.no_broker_application.Web_UI.dto.AuthResponse;
import com.no_broker_application.Web_UI.dto.UpdateUserRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

@FeignClient(name = "auth-service")
public interface AuthServiceClient {

    @PostMapping("/auth/google-login")
    AuthResponse googleLogin(@RequestBody AuthRequest request);

    @GetMapping("/auth/validate")
    AuthResponse validateToken(@RequestHeader("Authorization") String bearerToken);

    @PutMapping("/auth/users/{userId}")
    AuthResponse updateUser(@PathVariable("userId") Long userId, @RequestBody UpdateUserRequest request);

    @GetMapping("/auth/users/{userId}")
    AuthResponse getUserById(@PathVariable("userId") Long userId);

    @PutMapping("/auth/users/{userId}/subscription")
    AuthResponse updateSubscription(@PathVariable("userId") Long userId, @RequestBody Map<String, Object> request);
}
