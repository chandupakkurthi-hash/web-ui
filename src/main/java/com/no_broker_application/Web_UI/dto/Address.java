package com.no_broker_application.Web_UI.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class Address implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long addressId;
    private String city;
    private String locality;
    private String landmark;
    private Double latitude;
    private Double longitude;
}
