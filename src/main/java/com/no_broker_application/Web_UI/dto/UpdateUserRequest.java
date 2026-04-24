package com.no_broker_application.Web_UI.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateUserRequest {
    private String name;
    private String mobilePhone;
    private String profileImageUrl;
}

