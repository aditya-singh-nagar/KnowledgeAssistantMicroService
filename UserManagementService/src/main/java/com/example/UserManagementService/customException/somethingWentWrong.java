package com.example.UserManagementService.customException;


import com.example.UserManagementService.enums.SystemCode;

public class somethingWentWrong extends AppException {

    public somethingWentWrong(String message) {
        super(SystemCode.SYS_INTERNAL_ERROR, message);
    }

    public somethingWentWrong(SystemCode systemCode, String message) {
        super(systemCode, message);
    }
}
