package com.app.payment.controller;


import com.app.payment.model.FraudCheckRequest;
import com.app.payment.service.BrokerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger logger = LoggerFactory.getLogger(BrokerController.class);
    private BrokerService brokerService;
    public BrokerController(BrokerService brokerService) {
        this.brokerService = brokerService;
    }

    @PostMapping(consumes = "application/json", produces = "application/json")
    public ResponseEntity<String> processPayment(@RequestBody FraudCheckRequest requestDto) {
        logger.info("Request received to process payment with id : {} ", requestDto.getTransactionId());
        try {
            brokerService.processPayment(requestDto);
            return new ResponseEntity<>("Fraud check request processed successfully", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error processing fraud check request: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
