package com.chatbot.core.exception;

public class BadRequestException extends BaseException {

    private static final String ID = "BAD_REQUEST";

    public BadRequestException(String message) {
        super(ID, message, 400);
    }
}
