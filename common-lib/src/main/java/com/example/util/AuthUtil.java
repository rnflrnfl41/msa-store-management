package com.example.util;

import com.example.Constant.RoleConstants;
import com.example.exception.CommonException;
import com.example.exception.CommonExceptionCode;

public class AuthUtil {

    public static void validateAdmin(String role) {
        if (!RoleConstants.ROLE_ADMIN.equals(role)) {
            throw new CommonException(CommonExceptionCode.NO_PERMISSIONS);
        }
    }
}