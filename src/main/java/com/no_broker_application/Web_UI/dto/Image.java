package com.no_broker_application.Web_UI.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class Image implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long photoId;
    private String imageUrl;
}
