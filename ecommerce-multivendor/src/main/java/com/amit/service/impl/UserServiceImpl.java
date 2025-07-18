package com.amit.service.impl;

import com.amit.config.JwtProvider;
import com.amit.model.User;
import com.amit.repository.UserRepository;
import com.amit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;

    @Override
    public User findUserByJwtToken(String jwt) throws Exception {
        String email = jwtProvider.getEmailFromJwtToken(jwt);
        return this.findUserByEmail(email);
    }

    @Override
    public User findUserByEmail(String email) throws Exception {
        User user = userRepository.findByMail(email);
        if(user == null){
            throw new Exception("user not found with email: " + email);
        }
        return user;
    }
}
