package com.amit.service;

import com.amit.response.SignupRequest;

public interface AuthService {
     String createUser(SignupRequest req);
}
