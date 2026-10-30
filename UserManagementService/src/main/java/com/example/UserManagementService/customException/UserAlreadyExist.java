package com.example.UserManagementService.customException;


import com.example.UserManagementService.enums.SystemCode;

public class UserAlreadyExist extends AppException {

    public UserAlreadyExist(String message) {
        super(SystemCode.AUTH_USER_ALREADY_EXISTS, message);
    }

    public UserAlreadyExist(SystemCode systemCode, String message) {
        super(systemCode, message);
    }
}
