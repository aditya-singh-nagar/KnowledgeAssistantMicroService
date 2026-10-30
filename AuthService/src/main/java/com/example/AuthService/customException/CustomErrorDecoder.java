package com.example.AuthService.customException;

import feign.Response;
import feign.codec.ErrorDecoder;

public class CustomErrorDecoder implements ErrorDecoder {
    @Override
    public Exception decode(String methodKey, Response response) {
        if (response.status() == 404) {
            return   new RuntimeException("404 Not Found");
        }
        else{
            return   new RuntimeException("500 Internal Server Error");
        }
    }
}
