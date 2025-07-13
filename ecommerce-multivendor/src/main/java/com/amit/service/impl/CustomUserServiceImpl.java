package com.amit.service.impl;

import com.amit.domain.USER_ROLE;
import com.amit.model.Seller;
import com.amit.model.User;
import com.amit.repository.SellerRepository;
import com.amit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomUserServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;
    private static final String SELLER_PREFIX = "seller_"; //
    private final SellerRepository sellerRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if(username.startsWith(SELLER_PREFIX)){
            String actualUsername = username.substring(SELLER_PREFIX.length());
            Seller seller = sellerRepository.findByEmail(actualUsername);

            if(seller!=null){
                return buildUserDetails(seller.getEmail(), seller.getPassword(), seller.getRole());
            }
        }
        else{
            User user = userRepository.findByMail(username);
            if(user!=null){
                return buildUserDetails(user.getMail(),user.getPassword(), user.getRole());
            }
        }
        throw new UsernameNotFoundException("User or seller not found with email: " + username);
    }
    private UserDetails buildUserDetails(String email, String password, USER_ROLE role) {
            if(role==null){
                role = USER_ROLE.ROLE_CUSTOMER;
            }
            List<GrantedAuthority> authorities = new ArrayList<>();
            authorities.add(new SimpleGrantedAuthority("ROLE"+role));

            return new org.springframework.security.core.userdetails.User(
                    email,
                    password,
                    authorities
            );

    }
}
