package com.amit.model;

import lombok.Data;

@Data
public class BusinessDetails {
    private String businessName;
    private String businessAddress;
    private String businessMobile;
    private String businessEmail;
    private String logo;
    private String banner; // e.g., Retail, Wholesale, Service
}
