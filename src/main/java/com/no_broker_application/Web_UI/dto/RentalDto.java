package com.no_broker_application.Web_UI.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Getter
@Setter
public class RentalDto {
    private Boolean isSale;
    private Long expectedRent;
    private Long expectedDeposit;
    private String monthlyMaintenance;
    private Boolean negotiation;
    private Long price;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date availableFrom;

    private String availableFor;
    private String preferredTenets;
    private String furnishing;
    private String parking;
    private String description;
    private String propertyStatus;
}
