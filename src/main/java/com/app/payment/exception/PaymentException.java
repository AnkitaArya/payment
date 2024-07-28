package com.app.payment.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentException extends RuntimeException {
    private int statusCode;

    public PaymentException(String msg){
        super(msg);
    }

    public PaymentException(String msg, int statusCode){
        super(msg);
        this.statusCode=statusCode;
    }

    public PaymentException(String msg, int statusCode, Throwable cause){
        super(msg, cause);
        this.statusCode=statusCode;
    }
}
