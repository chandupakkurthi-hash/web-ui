package com.no_broker_application.Web_UI.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthResponse {
    private String token;
    private Long userId;
    private String name;
    private String email;
    private String mobilePhone;
    private String role;
    private Boolean isSubscribed;
    private String profileImageUrl;
}
