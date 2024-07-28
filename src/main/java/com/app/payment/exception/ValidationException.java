package com.app.payment.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ValidationException extends RuntimeException {
    private int statusCode;

    public ValidationException(String msg){
        super(msg);
    }

    public ValidationException(String msg, int statusCode){
        super(msg);
        this.statusCode=statusCode;
    }

    public ValidationException(String msg, int statusCode, Throwable cause){
        super(msg, cause);
        this.statusCode=statusCode;
    }
}
