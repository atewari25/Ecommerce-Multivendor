package com.amit.response;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class SignupRequest {
    private String email;
    private String fullName;
    private String otp;
}
