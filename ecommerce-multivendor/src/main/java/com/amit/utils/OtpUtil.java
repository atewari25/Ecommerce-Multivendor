package com.amit.utils;

import java.util.Random;

public class OtpUtil {
    public static String generateOtp(){
        int otpLength = 6;

        Random random = new Random();
        StringBuilder otp = new StringBuilder(otpLength);

        System.out.println(otp);// StringBuilder is more efficient for string concatenation

        for(int i=0;i<otpLength;i++){
            otp.append(random.nextInt(10));
        }
        return otp.toString();
    }
}
