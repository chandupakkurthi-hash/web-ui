package com.no_broker_application.Web_UI.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import java.io.Serializable;

@Getter
@Setter
public class User implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long userId;
    private String name;
    private String email;
    private String mobilePhone;
    private String role;
    private Boolean isSubscribed;
    private String profileImageUrl;
    private Set<Property> properties = new HashSet<>();
    private Set<Property> bookmarkedProperties = new HashSet<>();
    private List<Transaction> transactions;
}
