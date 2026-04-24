package com.no_broker_application.Web_UI.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class Amenity implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long amenityId;
    private int bathrooms;
    private Integer balcony;
    private String waterSupply;
    private Boolean petAllowed = false;
    private Boolean gym = false;
    private Boolean nonVeg = false;
    private Boolean gatedSecurity = false;
    private String showProperty;
    private String propertyCondition;
    private String secondaryNumber;
    private String nearByPlace;
    private Boolean lift = false;
    private Boolean gasPipeLine = false;
    private Boolean airConditioner = false;
    private Boolean park = false;
    private Boolean houseKeeping = false;
    private Boolean internetService = false;
    private Boolean powerBackUp = false;
    private Boolean serventRoom = false;
    private Boolean swimmingPool = false;
    private Boolean fireSafety = false;
}
