package com.no_broker_application.Web_UI.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class PropertyRequest {
    private Long propertyId;
    private String apartmentType;
    private String apartmentName;
    private Long bhkType;
    private Long floor;
    private Long totalFloors;
    private Long propertyAge;
    private String facing;
    private Double builtUpArea;
    private String availableFor;
    private Long expectedRent;
    private Long expectedDeposit;
    private String monthlyMaintenance;
    private String preferredTenets;
    private Boolean negotiation;
    private Date availableFrom;
    private String furnishing;
    private String parking;
    private String propertyStatus;
    private Long price;
    private Boolean isSale;
    private String description;
    private Long ownerId;

    private String city;
    private String locality;
    private String landmark;
    private Double latitude;
    private Double longitude;

    private Integer bathrooms;
    private Integer balcony;
    private String waterSupply;
    private Boolean petAllowed;
    private Boolean gym;
    private Boolean nonVeg;
    private Boolean gatedSecurity;
    private String showProperty;
    private String propertyCondition;
    private String secondaryNumber;
    private String nearByPlace;
    private Boolean lift;
    private Boolean gasPipeLine;
    private Boolean airConditioner;
    private Boolean park;
    private Boolean houseKeeping;
    private Boolean internetService;
    private Boolean powerBackUp;
    private Boolean serventRoom;
    private Boolean swimmingPool;
    private Boolean fireSafety;
}
