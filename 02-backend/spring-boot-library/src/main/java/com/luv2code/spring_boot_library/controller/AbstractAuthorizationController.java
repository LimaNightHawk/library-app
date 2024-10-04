package com.luv2code.spring_boot_library.controller;

import com.luv2code.spring_boot_library.utils.JwtParser;

public abstract class AbstractAuthorizationController {

    protected static final String ADMIN_ROLE = "admin";


    protected String getUserEmailFromToken(String token) {

        JwtParser jwtParser = new JwtParser(token);
        return jwtParser.getUserEmailFromToken();
    }

    protected String getUserType(String token) {

        JwtParser jwtParser = new JwtParser(token);
        return jwtParser.getUserType();
    }

    protected void assertAdminUser(String token) throws Exception {

        String userType = getUserType(token);
        if (!ADMIN_ROLE.equals(userType)) {
            throw new Exception("Administrators only");
        }
    }

}
