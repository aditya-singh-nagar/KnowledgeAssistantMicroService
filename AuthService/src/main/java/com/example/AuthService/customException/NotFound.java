package com.example.AuthService.customException;


import com.example.AuthService.enums.SystemCode;

public class NotFound extends AppException {

    public NotFound(String message) {
        super(SystemCode.SYS_NOT_FOUND, message);
    }

    public NotFound(SystemCode systemCode, String message) {
        super(systemCode, message);
    }
}
