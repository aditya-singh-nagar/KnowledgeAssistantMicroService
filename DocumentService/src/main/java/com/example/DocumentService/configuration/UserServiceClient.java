package com.example.DocumentService.configuration;

import com.example.DocumentService.dtos.LoginDto;
import com.example.DocumentService.dtos.RegisterResponseDto;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "UserManagementService"
//        , url = "http://localhost:8083"
        ,configuration = FeignClientConfig.class
)
public interface UserServiceClient {

    @GetMapping("/api/user/get/email/{email}")
    ResponseEntity<RegisterResponseDto> getUserByEmail (@PathVariable String email);

}
