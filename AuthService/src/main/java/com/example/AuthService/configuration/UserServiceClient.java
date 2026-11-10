package com.example.AuthService.configuration;

import com.example.AuthService.dtos.LoginDto;
import com.example.AuthService.dtos.RegisterRequestDto;
import com.example.AuthService.dtos.RegisterResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "UserManagementService"
        , url = "http://localhost:8083"
        ,configuration = FeignClientConfig.class
)
public interface UserServiceClient {

    @GetMapping("/api/product/{productId}")
    ResponseEntity<String> getProduct(@PathVariable("productId") Integer productId) ;

    @PostMapping("/api/user/add")
     ResponseEntity<RegisterResponseDto>  addUser (@RequestBody RegisterRequestDto registerRequestDto);

    @PostMapping("/api/user/login")
    ResponseEntity<RegisterResponseDto>  checkUser ( @RequestBody LoginDto loginDto);

    @GetMapping("/api/user/get/{userId}")
    ResponseEntity<RegisterResponseDto> getUser(@PathVariable("userId") int userId);
}
