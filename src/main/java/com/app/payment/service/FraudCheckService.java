package com.app.payment.service;

import com.app.payment.model.FraudCheckRequest;
import com.app.payment.model.FraudCheckResponse;
import com.app.payment.producer.FraudCheckProducer;
import com.app.payment.validate.FraudDetector;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class FraudCheckService {
    private static final Logger logger = LoggerFactory.getLogger(FraudCheckService.class);
    @Value("${jms.queuename.fraudcheck.response}")
    private String fraudCheckResponseQueueName;

    private XmlMapper xmlMapper;
    FraudCheckProducer fraudCheckProducer;

    public FraudCheckService(XmlMapper xmlMapper, FraudCheckProducer fraudCheckProducer) {
        this.xmlMapper = xmlMapper;
        this.fraudCheckProducer = fraudCheckProducer;
    }

    public void performFraudCheckAndSendToPublish(String xmlMessage) throws JsonProcessingException {
        FraudCheckRequest messageData = xmlMapper.readValue(xmlMessage, FraudCheckRequest.class);
        String status = FraudDetector.detectFraud(messageData) ? "FraudCheckRejected" : "FraudCheckApproved";
        FraudCheckResponse response = new FraudCheckResponse();
        response.setStatus(status);
        response.setTransactionId(messageData.getTransactionId());
        fraudCheckProducer.sendMessage(response, fraudCheckResponseQueueName);
    }
}
