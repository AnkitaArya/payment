package com.app.payment.consumer;

import com.app.payment.service.FraudCheckService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
public class FraudCheckConsumer {
    private static final Logger logger = LoggerFactory.getLogger(FraudCheckConsumer.class);

    FraudCheckService fraudCheckService;

    public FraudCheckConsumer(FraudCheckService fraudCheckService) {
        this.fraudCheckService = fraudCheckService;
    }

    @JmsListener(destination = "FraudCheckRequestQueue")
    public void receiveAndValidateMessage(String xmlMessage) throws JsonProcessingException {
        logger.info("Fraud check request received, payload {}", xmlMessage);
        fraudCheckService.performFraudCheckAndSendToPublish(xmlMessage);
    }



}
