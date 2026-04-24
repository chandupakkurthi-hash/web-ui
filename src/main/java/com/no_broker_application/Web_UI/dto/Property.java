package com.no_broker_application.Web_UI.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

import java.io.Serializable;

@Getter
@Setter
public class Property implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long propertyId;
    private String apartmentType;
    private String apartmentName;
    private Integer bhkType;
    private Integer floor;
    private Integer totalFloors;
    private Integer propertyAge;
    private String facing;
    private Double builtUpArea;
    private Boolean isSale;
    private Long expectedRent;
    private Long expectedDeposit;
    private String monthlyMaintenance;
    private Boolean negotiation;
    private Date availableFrom;
    private String availableFor;
    private String preferredTenets;
    private String furnishing;
    private String parking;
    private String description;
    private String propertyStatus;
    private Long price;
    private java.time.LocalDateTime createdAt;

    private Address address;
    private Amenity amenity;
    private Long ownerId;
    private List<Image> photos;
}
