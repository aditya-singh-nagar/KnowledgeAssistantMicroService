package com.example.AuthService.configuration;



import com.example.AuthService.customException.CustomErrorDecoder;
import com.example.AuthService.enums.SystemCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class RagFeignConfig {

    @Bean
    public ErrorDecoder ragErrorDecoder(
            ObjectMapper objectMapper) {

        Map<String, SystemCode> mappings = Map.of(

                "RAG_001", SystemCode.RAG_QUESTION_BLANK,

                "RAG_003", SystemCode.RAG_CHAT_SESSION_NOT_FOUND,

                "RAG_004", SystemCode.RAG_SERVICE_ERROR
        );

        return new CustomErrorDecoder(
                objectMapper,
                mappings
        );
    }
}
