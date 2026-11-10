package com.example.RagService.configuration;

import com.example.RagService.customException.CustomErrorDecoder;
import com.example.RagService.enums.SystemCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.RequestInterceptor;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
//import org.springframework.cloud.openfeign.RequestInterceptor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.Map;

@Configuration
public class FeignClientConfig {

    @Bean
    public RequestInterceptor requestInterceptor() {

        return requestTemplate -> {

            Authentication authentication =
                    SecurityContextHolder.getContext().getAuthentication();

            if (authentication instanceof JwtAuthenticationToken jwtAuth) {

                String token = jwtAuth.getToken().getTokenValue();

                requestTemplate.header(
                        HttpHeaders.AUTHORIZATION,
                        "Bearer " + token
                );
            }
        };
    }

    @Bean
    public ErrorDecoder userManagementErrorDecoder(
            ObjectMapper objectMapper) {

        Map<String, SystemCode> mappings = Map.ofEntries(

                // ==============================
                // USER / AUTHENTICATION ERRORS
                // ==============================

                Map.entry("AUTH_001", SystemCode.AUTH_INVALID_CREDENTIALS),

                Map.entry("AUTH_002", SystemCode.AUTH_USER_ALREADY_EXISTS),

                Map.entry("AUTH_003", SystemCode.AUTH_TOKEN_EXPIRED_OR_REVOKED),

                Map.entry("AUTH_004", SystemCode.AUTH_ACCESS_DENIED),

                Map.entry("AUTH_005", SystemCode.AUTH_ACCOUNT_NOT_ACTIVE),

                Map.entry("AUTH_006", SystemCode.AUTH_USER_NOT_FOUND),

                Map.entry("AUTH_007", SystemCode.AUTH_ROLE_NOT_FOUND),

                Map.entry("AUTH_008", SystemCode.AUTH_SELF_ADMIN_FORBIDDEN),

                Map.entry("AUTH_009", SystemCode.AUTH_APPROVAL_FORBIDDEN),


                // ==============================
                // DOCUMENT ERRORS
                // ==============================

                Map.entry("DOC_001", SystemCode.DOC_INVALID_FILE_TYPE),

                Map.entry("DOC_002", SystemCode.DOC_EMPTY_FILE),

                Map.entry("DOC_003", SystemCode.DOC_STORAGE_FAILED),

                Map.entry("DOC_004", SystemCode.DOC_NOT_FOUND),

                Map.entry("DOC_005", SystemCode.DOC_ACCESS_DENIED),

                Map.entry("DOC_006", SystemCode.DOC_INVALID_ACCESS_LEVEL),

                Map.entry("DOC_007", SystemCode.DOC_CONVERSION_FAILED),


                // ==============================
                // RAG ERRORS
                // ==============================

                Map.entry("RAG_001", SystemCode.RAG_QUESTION_BLANK),

                Map.entry("RAG_003", SystemCode.RAG_CHAT_SESSION_NOT_FOUND),

                Map.entry("RAG_004", SystemCode.RAG_SERVICE_ERROR),


                // ==============================
                // SYSTEM ERRORS
                // ==============================

                Map.entry("SYS_001", SystemCode.SYS_INTERNAL_ERROR),

                Map.entry("SYS_002", SystemCode.SYS_NOT_FOUND),

                Map.entry("SYS_003", SystemCode.SYS_BAD_REQUEST),

                Map.entry("SYS_004", SystemCode.SYS_VALIDATION_FAILED) ,

                Map.entry("SYS_005", SystemCode.SYS_DOWNSTREAM_FAILURE),

                Map.entry("SYS_006", SystemCode.SYS_SERVICE_UNAVAILABLE)
        );

        return new CustomErrorDecoder(
                objectMapper,
                mappings
        );
    }
}