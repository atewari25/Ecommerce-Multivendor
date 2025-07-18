package com.amit.controller;

import com.amit.domain.USER_ROLE;
import com.amit.repository.UserRepository;
import com.amit.request.LoginOtpRequest;
import com.amit.request.LoginRequest;
import com.amit.response.ApiResponse;
import com.amit.response.AuthResponse;
import com.amit.response.SignupRequest;
import com.amit.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor // This annotation generates a constructor with required arguments for the class
public class AuthController {

    private final UserRepository userRepository;
    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> createUserHandler(@RequestBody SignupRequest req) throws Exception {
      String jwt = authService.createUser(req);

        AuthResponse authResponse = new AuthResponse();
        authResponse.setJwt(jwt);
        authResponse.setMessage("Register Successfully");
        authResponse.setRole(USER_ROLE.ROLE_CUSTOMER );

        return ResponseEntity.ok(authResponse);
    }

    @PostMapping("/sent/login-signup-otp")
    public ResponseEntity<ApiResponse> otpHandler(@RequestBody LoginOtpRequest req) throws Exception {

        authService.sentLoginOtp(req.getEmail(), req.getRole());
        ApiResponse res = new ApiResponse();
        res.setMessage("otp sent Successfully");

        return ResponseEntity.ok(res);
    }

    @PostMapping("/signing")
    public ResponseEntity<AuthResponse> loginHandler(@RequestBody LoginRequest req) throws Exception {

       AuthResponse authResponse =  authService.signing(req);

        return ResponseEntity.ok(authResponse);
    }
}
