package com.example.AuthService.customException;


import com.example.AuthService.enums.SystemCode;

public class UserAlreadyExist extends AppException {

    public UserAlreadyExist(String message) {
        super(SystemCode.AUTH_USER_ALREADY_EXISTS, message);
    }

    public UserAlreadyExist(SystemCode systemCode, String message) {
        super(systemCode, message);
    }
}
