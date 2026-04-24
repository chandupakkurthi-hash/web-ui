package com.no_broker_application.Web_UI.security;

import com.no_broker_application.Web_UI.dto.User;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
@RequiredArgsConstructor
public class CurrentUserModelAdvice {

    private final CurrentUserService currentUserService;

    @ModelAttribute("user")
    public User currentUser() {
        return currentUserService.getCurrentUserOrNull();
    }
}

