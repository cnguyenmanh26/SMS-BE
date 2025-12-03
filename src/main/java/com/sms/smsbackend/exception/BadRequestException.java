package com.sms.smsbackend.exception;

import lombok.Getter;

@Getter
public class BadRequestException extends RuntimeException {
    private final String field;
    private final Object rejectedValue;

    public BadRequestException(String message) {
        super(message);
        this.field = null;
        this.rejectedValue = null;
    }

    public BadRequestException(String message, String field, Object rejectedValue) {
        super(message);
        this.field = field;
        this.rejectedValue = rejectedValue;
    }
}
