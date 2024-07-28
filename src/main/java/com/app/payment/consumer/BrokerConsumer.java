package com.app.payment.consumer;


import com.app.payment.service.BrokerService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
public class BrokerConsumer {
    private static final Logger logger = LoggerFactory.getLogger(BrokerConsumer.class);
    private final BrokerService brokerService;
    public BrokerConsumer(BrokerService brokerService) {
        this.brokerService = brokerService;
    }

    @JmsListener(destination = "FraudCheckResponseQueue")
    public void receiveResponse(String xmlResponse) {
        logger.info("FraudDetectionResponse Received");
        brokerService.processFraudCheckResponse(xmlResponse);
    }
    @JmsListener(destination = "BrokerRequestQueue")
    public void receivePaymentRequest(String xmlResponse) throws JsonProcessingException {
        logger.info("Payment Request Received");
        brokerService.processPaymentV2(xmlResponse);
    }
}
