package com.no_broker_application.Web_UI.security;

import com.no_broker_application.Web_UI.client.AuthServiceClient;
import com.no_broker_application.Web_UI.dto.AuthResponse;
import com.no_broker_application.Web_UI.dto.User;
import feign.FeignException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Service
@RequiredArgsConstructor
public class CurrentUserService {

    private static final String JWT_COOKIE_NAME = "jwt_token";

    private final AuthServiceClient authServiceClient;

    public User getCurrentUserOrNull() {
        HttpServletRequest request = currentRequestOrNull();
        if (request == null) return null;

        String token = extractJwtFromCookies(request);
        if (token == null || token.isBlank()) return null;

        try {
            AuthResponse auth = authServiceClient.validateToken("Bearer " + token);
            if (auth == null || auth.getUserId() == null) return null;

            User user = new User();
            user.setUserId(auth.getUserId());
            user.setName(auth.getName());
            user.setEmail(auth.getEmail());
            user.setMobilePhone(auth.getMobilePhone());
            user.setRole(auth.getRole());
            user.setIsSubscribed(auth.getIsSubscribed());
            user.setProfileImageUrl(auth.getProfileImageUrl());
            return user;
        } catch (FeignException e) {
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    private HttpServletRequest currentRequestOrNull() {
        RequestAttributes attrs = RequestContextHolder.getRequestAttributes();
        if (!(attrs instanceof ServletRequestAttributes servletAttrs)) {
            return null;
        }
        return servletAttrs.getRequest();
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

