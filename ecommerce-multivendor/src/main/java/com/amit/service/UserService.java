package com.amit.service;

import com.amit.model.User;

public interface UserService {
     User findUserByJwtToken(String jwt) throws Exception;
     User findUserByEmail(String email) throws Exception;
}
