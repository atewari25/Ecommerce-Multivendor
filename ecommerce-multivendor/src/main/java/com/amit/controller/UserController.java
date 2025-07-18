package com.amit.controller;

import com.amit.model.User;
import com.amit.model.VerificationCode;
import com.amit.request.LoginOtpRequest;
import com.amit.response.ApiResponse;
import com.amit.service.AuthService;
import com.amit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final AuthService authService;

    @GetMapping("/users/profile")
    public ResponseEntity<User> createUserHandler(@RequestHeader ("Authorization") String jwt) throws Exception {
        User user = userService.findUserByJwtToken(jwt);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/sent/login-otp")
    public ResponseEntity<ApiResponse> otpHandler(@RequestBody LoginOtpRequest req) throws Exception {
        authService.sentLoginOtp(req.getEmail(), req.getRole());

        ApiResponse res = new ApiResponse();

        res.setMessage("otp sent Successfully");


        return ResponseEntity.ok(res);
    }
}
