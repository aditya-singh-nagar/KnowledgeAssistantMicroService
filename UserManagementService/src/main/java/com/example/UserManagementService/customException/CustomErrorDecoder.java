package com.example.UserManagementService.customException;



import com.example.UserManagementService.enums.SystemCode;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import feign.Response;
import feign.codec.ErrorDecoder;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class CustomErrorDecoder implements ErrorDecoder {

    private final ObjectMapper objectMapper;

    private final Map<String, SystemCode> errorMappings;

    @Override
    public Exception decode(String methodKey, Response response) {

        Response.Body body = response.body();

        log.info("========== FEIGN ERROR RESPONSE ==========");

        log.info("Method: {}", methodKey);

        log.info("HTTP Status: {}", response.status());

        log.info("Response headers: {}", response.headers());

        log.info("Body is null: {}", body == null);

        log.info(
                "Body length: {}",
                body != null ? body.length() : null
        );

        log.info(
                "Body repeatable: {}",
                body != null && body.isRepeatable()
        );

        log.info("==========================================");

        log.info(
                "Custom ErrorDecoder invoked | Method: {} | Status: {}",
                methodKey,
                response.status()
        );

        if (response.body() == null) {

            log.error("Downstream response body is empty");

            return new AppException(
                    SystemCode.SYS_DOWNSTREAM_FAILURE
            );
        }

        try (InputStream inputStream =
                     response.body().asInputStream()) {

            // Read the complete response as JSON
            JsonNode root = objectMapper.readTree(inputStream);

            if (root == null || !root.isObject()) {

                log.error(
                        "Invalid downstream JSON response. Method: {}, Response: {}",
                        methodKey,
                        root
                );

                return new AppException(
                        SystemCode.SYS_DOWNSTREAM_FAILURE
                );
            }

            // Extract code and message from ApiResponse
            String remoteCode =
                    root.path("code").asText(null);

            String remoteMessage =
                    root.path("message").asText(null);

            log.info(
                    "Downstream response received | Code: {} | Message: {}",
                    remoteCode,
                    remoteMessage
            );

            // Validate error code
            if (remoteCode == null || remoteCode.isBlank()) {

                log.error(
                        "Error code missing in downstream response. JSON fields: {}",
                        root.propertyStream()
                                .map(Map.Entry::getKey)
                                .toList()
                );

                return new AppException(
                        SystemCode.SYS_DOWNSTREAM_FAILURE
                );
            }

            // Find the corresponding local error
            SystemCode localCode =
                    errorMappings.get(remoteCode);

            // Unknown error code
            if (localCode == null) {

                log.error(
                        "Unmapped downstream error | Remote Code: {} | Available Codes: {}",
                        remoteCode,
                        errorMappings.keySet()
                );

                return new AppException(
                        SystemCode.SYS_DOWNSTREAM_FAILURE
                );
            }

            log.info(
                    "Error successfully mapped | Remote: {} | Local: {}",
                    remoteCode,
                    localCode.getCode()
            );

            return new AppException(localCode);

        } catch (IOException ex) {

            log.error(
                    "Failed to read downstream error response. Method: {}, Status: {}",
                    methodKey,
                    response.status(),
                    ex
            );

            return new AppException(
                    SystemCode.SYS_DOWNSTREAM_FAILURE
            );
        }
    }
}