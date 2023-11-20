package com.chatbot.core.exception;

public class NotFoundException extends BaseException {

    private static final String ID = "NOT_FOUND";

    public NotFoundException(String message) {
        super(ID, message, 404);
    }
}
