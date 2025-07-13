package com.amit.service.impl;

import com.amit.config.JwtProvider;
import com.amit.domain.USER_ROLE;
import com.amit.model.Cart;
import com.amit.model.User;
import com.amit.repository.CartRepository;
import com.amit.repository.UserRepository;
import com.amit.response.SignupRequest;
import com.amit.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CartRepository cartRepository;
    private final JwtProvider jwtProvider;

    @Override
    public String createUser(SignupRequest req) {
        User user = userRepository.findByMail(req.getEmail());
        if(user==null) {
            User createdUser = new User();
            createdUser.setMail(req.getEmail());
            createdUser.setFullName(req.getFullName());
            createdUser.setRole(USER_ROLE.ROLE_CUSTOMER);
            createdUser.setMobile("876542");
            createdUser.setPassword(passwordEncoder.encode(req.getOtp()));

            userRepository.save(createdUser); // Save the user to the database

            Cart cart = new Cart();
            cart.setUser(createdUser); // Associate the cart with the user
            cartRepository.save(cart);
        }

            List<GrantedAuthority> authorities = new ArrayList<>();
            authorities.add(new SimpleGrantedAuthority(USER_ROLE.ROLE_CUSTOMER.toString())); // Add the user's role as an authority

            Authentication authentication = new UsernamePasswordAuthenticationToken(req.getEmail(),null, authorities); // Create an authentication object with the user's email, password and authorities
            SecurityContextHolder.getContext().setAuthentication(authentication);


        return jwtProvider.generateToken(authentication); // Generate a JWT token for the authenticated user
    }
}






