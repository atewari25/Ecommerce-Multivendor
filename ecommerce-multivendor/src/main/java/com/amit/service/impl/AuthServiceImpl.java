package com.amit.service.impl;

import com.amit.config.JwtProvider;
import com.amit.domain.USER_ROLE;
import com.amit.model.Cart;
import com.amit.model.Seller;
import com.amit.model.User;
import com.amit.model.VerificationCode;
import com.amit.repository.CartRepository;
import com.amit.repository.SellerRepository;
import com.amit.repository.UserRepository;
import com.amit.repository.VerificationCodeRepository;
import com.amit.request.LoginRequest;
import com.amit.response.AuthResponse;
import com.amit.response.SignupRequest;
import com.amit.service.AuthService;
import com.amit.service.EmailService;
import com.amit.utils.OtpUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
 public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CartRepository cartRepository;
    private final JwtProvider jwtProvider;
    private final VerificationCodeRepository verificationCodeRepository;
    private final EmailService emailService;
    private final CustomUserServiceImpl customUserService;
    private final SellerRepository sellerRepository;

    @Override
    public void sentLoginOtp(String email, USER_ROLE role) throws Exception {
        String SIGNING_PREFIX = "signing_";

        if(email.startsWith(SIGNING_PREFIX)){


            if(role == USER_ROLE.ROLE_SELLER){
                Seller seller = sellerRepository.findByEmail(email);
                if(seller == null){
                    throw new Exception("Seller not found with email: " + email);
                }
            }
            else{
                User user = userRepository.findByMail(email);
                if(user == null){
                    throw new Exception("User not found with email: " + email);
                }
            }


        }

        VerificationCode isExists = verificationCodeRepository.findByEmail(email);
        if(isExists!=null){
             verificationCodeRepository.delete(isExists);
        }

        String otp = OtpUtil.generateOtp();
        VerificationCode verificationCode = new VerificationCode();
        verificationCode.setOtp(otp);
        verificationCode.setEmail(email);
        verificationCodeRepository.save(verificationCode); // Save the OTP and email for verification to the database

        String subject = "Baazar - Login OTP";
        String text = "Your OTP for login is: " + otp;

        emailService.sendVerificationOtpEmail(email, otp, subject, text); // Send the OTP via email


    }

    @Override
    public String createUser(SignupRequest req) throws Exception {
        VerificationCode verificationCode =  verificationCodeRepository.findByEmail(req.getEmail());

        if(verificationCode == null || !verificationCode.getOtp().equals(req.getOtp())){
            throw new Exception("wrong otp...");
        }

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

    @Override
    public AuthResponse signing(LoginRequest req) throws Exception {
        String userName = req.getEmail();
        String otp = req.getOtp();

        Authentication authentication = authenticate(userName, otp);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = jwtProvider.generateToken(authentication);

        AuthResponse authResponse = new AuthResponse();
        authResponse.setJwt(token);
        authResponse.setMessage("Login Successfully");

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        String roleName = authorities.isEmpty()?null:authorities.iterator().next().getAuthority();

        authResponse.setRole(USER_ROLE.valueOf(roleName));
        return authResponse;
    }

    private Authentication authenticate(String userName, String otp) throws Exception {
        UserDetails userDetails = customUserService.loadUserByUsername(userName);

        String SELLER_PREFIX = "seller_";
        if(userName.startsWith(SELLER_PREFIX)){
            userName = userName.substring(SELLER_PREFIX.length());
        }

        if(userDetails==null){
            throw new BadCredentialsException("Invalid username or password");
        }
        VerificationCode verificationCode = verificationCodeRepository.findByEmail(userName);
        if(verificationCode==null || !verificationCode.getOtp().equals(otp)){
            throw new Exception("Wrong otp...");
        }

        return new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );
    }
}






