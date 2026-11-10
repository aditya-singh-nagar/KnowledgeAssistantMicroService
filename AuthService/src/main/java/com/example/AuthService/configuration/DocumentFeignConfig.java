package com.example.AuthService.configuration;



import com.example.AuthService.customException.CustomErrorDecoder;
import com.example.AuthService.enums.SystemCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class DocumentFeignConfig {

    @Bean
    public ErrorDecoder documentErrorDecoder(
            ObjectMapper objectMapper) {

        Map<String, SystemCode> mappings = Map.of(

                "DOC_001", SystemCode.DOC_INVALID_FILE_TYPE,

                "DOC_002", SystemCode.DOC_EMPTY_FILE,

                "DOC_003", SystemCode.DOC_STORAGE_FAILED,

                "DOC_004", SystemCode.DOC_NOT_FOUND,

                "DOC_005", SystemCode.DOC_ACCESS_DENIED,

                "DOC_006", SystemCode.DOC_INVALID_ACCESS_LEVEL,

                "DOC_007", SystemCode.DOC_CONVERSION_FAILED
        );

        return new CustomErrorDecoder(
                objectMapper,
                mappings
        );
    }
}
