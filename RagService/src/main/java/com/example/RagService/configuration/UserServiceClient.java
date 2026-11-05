package com.example.RagService.configuration;


import com.example.RagService.dtos.RegisterResponseDto;
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

    @GetMapping("/api/user/get/{userId}")
    ResponseEntity<RegisterResponseDto> getUser(@PathVariable("userId") int userId);
}
