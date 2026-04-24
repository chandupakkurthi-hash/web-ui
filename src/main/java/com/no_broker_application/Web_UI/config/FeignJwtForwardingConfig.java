package com.no_broker_application.Web_UI.config;

import feign.RequestInterceptor;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Configuration
public class FeignJwtForwardingConfig {

    private static final String JWT_COOKIE_NAME = "jwt_token";

    @Bean
    public RequestInterceptor jwtForwardingInterceptor() {
        return template -> {
            if (template.headers().containsKey(HttpHeaders.AUTHORIZATION)) {
                return;
            }

            RequestAttributes attrs = RequestContextHolder.getRequestAttributes();
            if (!(attrs instanceof ServletRequestAttributes servletAttrs)) {
                return;
            }

            HttpServletRequest request = servletAttrs.getRequest();
            if (request == null) return;

            String token = extractJwtFromCookies(request);
            if (token == null || token.isBlank()) return;

            template.header(HttpHeaders.AUTHORIZATION, "Bearer " + token);
        };
    }

    private String extractJwtFromCookies(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) return null;
        for (Cookie cookie : cookies) {
            if (cookie == null) continue;
            if (JWT_COOKIE_NAME.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }
}
