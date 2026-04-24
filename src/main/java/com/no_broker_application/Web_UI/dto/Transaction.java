package com.no_broker_application.Web_UI.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

import java.io.Serializable;

@Getter
@Setter
public class Transaction implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long id;
    private Long amount;
    private LocalDateTime paymentTime;
    private String paymentStatus;
}
