package com.app.payment.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class PaymentExceptionHandler {
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGlobalException(Exception ex){
        ErrorDetails error = new ErrorDetails(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleIllegalArguementException(IllegalArgumentException ex){
        ErrorDetails error = new ErrorDetails(ex.getMessage(), HttpStatus.BAD_REQUEST.value());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(PaymentException.class)
    public ResponseEntity<?> handlePaymentException(PaymentException ex){
        if(ex.getStatusCode()==0){
            ex.setStatusCode(HttpStatus.BAD_REQUEST.value());
        }
        ErrorDetails error = new ErrorDetails(ex.getMessage(), ex.getStatusCode());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<?> handleValidationException(ValidationException ex){
        if(ex.getStatusCode()==0){
            ex.setStatusCode(HttpStatus.BAD_REQUEST.value());
        }
        ErrorDetails error = new ErrorDetails(ex.getMessage(), ex.getStatusCode());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
}
