package com.amit.controller;

import com.amit.config.JwtProvider;
import com.amit.domain.AccountStatus;
import com.amit.model.Seller;
import com.amit.model.SellerReport;
import com.amit.model.VerificationCode;
import com.amit.repository.VerificationCodeRepository;
import com.amit.request.LoginRequest;
import com.amit.response.ApiResponse;
import com.amit.response.AuthResponse;
import com.amit.service.AuthService;
import com.amit.service.EmailService;
import com.amit.service.SellerService;
import com.amit.utils.OtpUtil;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/sellers")
public class SellerController {
    private final SellerService sellerService;
    private final VerificationCodeRepository verificationCodeRepository;
    private final AuthService authService;
    private final EmailService emailService;
    private final JwtProvider jwtProvider;

    @PostMapping("/login")
    ResponseEntity<AuthResponse> loginSeller(@RequestBody LoginRequest req) throws Exception {
        String otp = req.getOtp();
        String email = req.getEmail();

        req.setEmail("seller_" + email);
        AuthResponse authResponse = authService.sign ing(req);

        return ResponseEntity.ok(authResponse);
    }

    @PatchMapping("/verify/{otp}")
    public ResponseEntity<Seller> verifySellerEmail(@PathVariable String otp) throws Exception{
        VerificationCode verificationCode = verificationCodeRepository.findByOtp(otp);

        if(verificationCode == null || !verificationCode.getOtp().equals(otp)) {
            throw new Exception("Wrong OTP..");
        }

        Seller seller = sellerService.verifyEmail(verificationCode.getEmail(),otp);
        return new ResponseEntity<>(seller, HttpStatus.OK);

    }

    @PostMapping
    public ResponseEntity<Seller> createSeller(@RequestBody Seller seller) throws Exception, MessagingException {
        Seller savedSeller = sellerService.createSeller(seller);

        String otp = OtpUtil.generateOtp();
        VerificationCode verificationCode = new VerificationCode();
        verificationCode.setOtp(otp);
        verificationCode.setEmail(seller.getEmail());
        verificationCodeRepository.save(verificationCode); // Save the OTP and email for verification to the database

        String subject = "Baazaar Email Verification code";
        String text = "Welcome to Baazar, please verify your email using the following link";
        String frontendUrl = "http://localhost:3000/verify-seller/";
        emailService.sendVerificationOtpEmail(seller.getEmail(), otp, subject, text+ frontendUrl);

        return new ResponseEntity<>(savedSeller, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Seller> getSellerById(@PathVariable Long id) throws Exception {
        Seller seller = sellerService.getSellerById(id);
        return new ResponseEntity<>(seller, HttpStatus.OK);
    }


    @GetMapping("/profile")  // Endpoint to get the seller profile using JWT
    public ResponseEntity<Seller> getSellerByJwt(@RequestHeader("Authorization") String jwt) throws Exception {
        Seller seller = sellerService.getSellerProfile(jwt);
        return new ResponseEntity<>(seller, HttpStatus.OK);
    }

//    @GetMapping("/report")
//    public ResponseEntity<SellerReport> getSellerReport(@RequestHeader("Authorization") String jwt) throws Exception {
//        String email = jwtProvider.getEmailFromJwtToken(jwt);
//        Seller seller = sellerService.getSellerByEmail(email);
//        SellerReport sellerReport = sellerReportService.getSellerReport(seller);
//
//        return new ResponseEntity<>(sellerReport, HttpStatus.OK);
//    }

    @GetMapping("/getAllSellers")
    public ResponseEntity<List<Seller>> getAllSellers(@RequestParam(required = false)AccountStatus status){
        List<Seller> sellers = sellerService.getAllSellers(status);
        return new ResponseEntity<>(sellers, HttpStatus.OK);
    }

    @PatchMapping()
    public ResponseEntity<Seller> updateSeller(@RequestHeader("Authorization") String jwt, @RequestBody Seller seller) throws Exception {
        Seller profile = sellerService.getSellerProfile(jwt);
        Seller updatedSeller = sellerService.updateSeller(profile.getId(), seller);
        return new ResponseEntity<>(updatedSeller, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteSeller(@PathVariable Long id) throws Exception {
        sellerService.deleteSeller(id);
        return ResponseEntity.noContent().build();
    }

}
