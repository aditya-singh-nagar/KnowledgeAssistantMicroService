package com.example.AuthService.configuration;


import com.example.AuthService.customException.CustomErrorDecoder;
import com.example.AuthService.enums.SystemCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class UserManagementFeignConfig {

//    @Bean
//    public ErrorDecoder userManagementErrorDecoder(
//            ObjectMapper objectMapper) {
//
//        Map<String, SystemCode> mappings = Map.of(
//
//                "AUTH_006", SystemCode.AUTH_USER_NOT_FOUND,
//
//                "AUTH_007", SystemCode.AUTH_ROLE_NOT_FOUND,
//
//                "AUTH_002", SystemCode.AUTH_USER_ALREADY_EXISTS,
//
//                "AUTH_004", SystemCode.AUTH_ACCESS_DENIED,
//
//                "AUTH_005", SystemCode.AUTH_ACCOUNT_NOT_ACTIVE
//        );
//
//        return new CustomErrorDecoder(
//                objectMapper,
//                mappings
//        );
//    }


}
