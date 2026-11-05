package com.example.DocumentService.customException;


import com.example.DocumentService.enums.SystemCode;

public class NotFound extends AppException {

    public NotFound(String message) {
        super(SystemCode.SYS_NOT_FOUND, message);
    }

    public NotFound(SystemCode systemCode, String message) {
        super(systemCode, message);
    }
}
