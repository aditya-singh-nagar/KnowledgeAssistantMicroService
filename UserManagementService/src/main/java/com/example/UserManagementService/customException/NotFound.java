package com.example.UserManagementService.customException;


import com.example.UserManagementService.enums.SystemCode;

public class NotFound extends AppException {

    public NotFound(String message) {
        super(SystemCode.SYS_NOT_FOUND, message);
    }

    public NotFound(SystemCode systemCode, String message) {
        super(systemCode, message);
    }
}
