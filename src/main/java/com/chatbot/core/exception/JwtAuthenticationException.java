package com.chatbot.core.exception;

public class JwtAuthenticationException extends BaseException {

    private static final String ID = "JWT_AUTHENTICATION_EXCEPTION";

    public JwtAuthenticationException(String message) {
        super(ID, message, 401);
    }
}
