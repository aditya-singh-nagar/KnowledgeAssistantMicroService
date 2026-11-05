package com.example.RagService.customException;


import com.example.RagService.enums.SystemCode;

public class NotFound extends AppException {

    public NotFound(String message) {
        super(SystemCode.SYS_NOT_FOUND, message);
    }

    public NotFound(SystemCode systemCode, String message) {
        super(systemCode, message);
    }
}
