package com.app.payment.controller;

import com.app.payment.entity.Payment;
import com.app.payment.model.PaymentRequestDTO;
import com.app.payment.service.PaymentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = "/api/v2/payment", produces = {APPLICATION_JSON_VALUE})
public class PaymentControllerV2 {
    private static final Logger logger = LoggerFactory.getLogger(PaymentControllerV2.class);

    private PaymentService paymentService;

    public PaymentControllerV2(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping(consumes = "application/json", produces = "application/json")
    public ResponseEntity<String> createPayment(@RequestBody PaymentRequestDTO requestDto){
        logger.info("Request received to create payment with id : {} ", requestDto.getTransactionId());
        String response = paymentService.processPaymentV2(requestDto);
        if (response.startsWith("Invalid")) {
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        } else if (response.contains("failed")) {
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        } else {
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        }
    }

    @GetMapping(value = "/{paymentId}", produces = "application/json")
    public ResponseEntity<Payment> fetchPayment(@PathVariable String paymentId) {
        logger.info("Request received to fetch status of payment with id : {} ", paymentId);
        Payment result = paymentService.getPaymentStatus(paymentId);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
