package com.amit.controller;

import com.amit.response.ApiResponse;
import org.hibernate.metamodel.internal.AbstractPojoInstantiator;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {
    @GetMapping("/home")
    public ApiResponse HomeControllerHandler(){
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setMessage("Welcome to the E-commerce Multivendor Application");
        return apiResponse;
    }
}
