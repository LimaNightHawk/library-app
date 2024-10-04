package com.luv2code.spring_boot_library.controller;

import com.luv2code.spring_boot_library.utils.JwtParser;

public abstract class AbstractAuthorizationController {


    protected String getUserEmailFromToken(String token) {

        JwtParser jwtParser = new JwtParser(token);
        return jwtParser.getUserEmailFromToken();
    }

}
