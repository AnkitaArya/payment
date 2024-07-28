package com.app.payment.controller;

import com.app.payment.entity.Payment;
import com.app.payment.model.FraudCheckResponse;
import com.app.payment.model.PaymentRequestDTO;
import com.app.payment.service.PaymentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = "/api/v1/payment", produces = {APPLICATION_JSON_VALUE})
public class PaymentControllerV1 {
    private static final Logger logger = LoggerFactory.getLogger(PaymentControllerV1.class);

    private PaymentService paymentService;

    public PaymentControllerV1(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping(consumes = "application/json", produces = "application/json")
    public ResponseEntity<String> createPayment(@RequestBody PaymentRequestDTO requestDto) throws IOException, InterruptedException {
        logger.info("Request received to create payment with id : {} ", requestDto.getTransactionId());
        String response = paymentService.processPayment(requestDto);
        if (response.startsWith("Invalid")) {
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        } else if (response.contains("failed")) {
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        } else {
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        }
    }

    @GetMapping(value = "/{paymentId}", produces = "application/json")
    public ResponseEntity<Payment> fetchPayment(@PathVariable UUID paymentId) {
        logger.info("Request received to fetch status of payment with id : {} ", paymentId);
        Payment result = paymentService.getPaymentStatus(paymentId);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PutMapping
    public ResponseEntity<Payment> updatePaymentStatus(@RequestBody FraudCheckResponse response) {
        logger.info("Received request to update payment status for id: {}", response.getTransactionId());
        Payment updatedPayment = paymentService.updatePaymentStatus(response);
        return ResponseEntity.ok(updatedPayment);

    }


}
