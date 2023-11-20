package com.chatbot.core.exception;

public class LoginForbiddenException extends BaseException {

    private static final String ID = "USER_ACCESS";
    private static final String DEFAULT_MESSAGE = "Access forbidden";

    public LoginForbiddenException(String message) {
        super(ID, message, 403);
    }

    public LoginForbiddenException() {
        super(ID, DEFAULT_MESSAGE, 403);
    }
}
