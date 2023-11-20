package com.chatbot.core.exception;

import java.util.Objects;

public class BaseException extends RuntimeException {

    private final String id;
    private final int httpStatus;

    public BaseException(String id, String message, int httpStatus) {
        super(message);
        this.id = Objects.requireNonNull(id).toUpperCase();
        this.httpStatus = httpStatus;
    }

    public BaseException(String id, String message, int httpStatus, Exception e) {
        super(message, e);
        this.id = Objects.requireNonNull(id).toUpperCase();
        this.httpStatus = httpStatus;
    }

    public String getId() {
        return id;
    }

    public int getHttpStatus() {
        return httpStatus;
    }

}
