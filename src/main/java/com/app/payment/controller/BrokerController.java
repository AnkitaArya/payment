package com.app.payment.controller;


import com.app.payment.model.PaymentRequestDTO;
import com.app.payment.model.PaymentResponseDTO;
import com.app.payment.service.BrokerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = "/api/v1/broker", produces = {APPLICATION_JSON_VALUE})
public class BrokerController {

    @Autowired
    private BrokerService brokerService;

    @PostMapping(value = "/process/request/data", consumes = "application/json", produces = "application/json")
    public ResponseEntity<String> createPayment(@RequestBody PaymentRequestDTO requestDto) {
        try {
            brokerService.processPayment(requestDto);
            return new ResponseEntity<>("Fraud check request processed successfully", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error processing fraud check request: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
