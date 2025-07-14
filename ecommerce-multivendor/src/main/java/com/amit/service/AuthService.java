package com.amit.service;

import com.amit.response.SignupRequest;

public interface AuthService {

     void sentLoginOtp(String email) throws Exception;
     String createUser(SignupRequest req) throws Exception;
}
