package com.amit.service;

import com.amit.request.LoginRequest;
import com.amit.response.AuthResponse;
import com.amit.response.SignupRequest;

public interface AuthService {

     void sentLoginOtp(String email) throws Exception;
     String createUser(SignupRequest req) throws Exception;

     // method for login user
     AuthResponse signing(LoginRequest req);

}
