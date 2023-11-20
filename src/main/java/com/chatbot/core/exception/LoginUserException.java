package com.chatbot.core.exception;

public class LoginUserException extends BaseException {

    private static final String ID = "LOGIN_USER";

    public LoginUserException(String message) {
        super(ID, message, 401);
    }
}
