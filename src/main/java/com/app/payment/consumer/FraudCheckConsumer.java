package com.app.payment.consumer;

import com.app.payment.service.FraudCheckService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
public class FraudCheckConsumer {
    private static final Logger logger = LoggerFactory.getLogger(FraudCheckConsumer.class);

    @Value("${jms.queuename.fraudcheck.response.v1}")
    private String fraudCheckResponseQueueNameV1;

    @Value("${jms.queuename.fraudcheck.response.v2}")
    private String fraudCheckResponseQueueNameV2;
    FraudCheckService fraudCheckService;


    public FraudCheckConsumer(FraudCheckService fraudCheckService) {
        this.fraudCheckService = fraudCheckService;
    }

    @JmsListener(destination = "FraudCheckRequestQueueV1")
    public void receiveAndValidateMessageV1(String xmlMessage) throws JsonProcessingException {
        logger.info("Fraud check request received, payload {}", xmlMessage);
        fraudCheckService.performFraudCheckAndSendToPublish(xmlMessage, fraudCheckResponseQueueNameV1);
    }

    @JmsListener(destination = "FraudCheckRequestQueueV2")
    public void receiveAndValidateMessageV2(String xmlMessage) throws JsonProcessingException {
        logger.info("Fraud check request received, payload {}", xmlMessage);
        fraudCheckService.performFraudCheckAndSendToPublish(xmlMessage,fraudCheckResponseQueueNameV2);
    }



}
