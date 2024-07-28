package com.app.payment.consumer;

import com.app.payment.model.FraudCheckResponse;
import com.app.payment.service.BrokerService;
import com.app.payment.service.PaymentService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
public class PaymentConsumer {
    private static final Logger logger = LoggerFactory.getLogger(PaymentConsumer.class);
    private PaymentService paymentService;

    private XmlMapper xmlMapper;

    public PaymentConsumer(PaymentService paymentService,XmlMapper xmlMapper) {
        this.paymentService = paymentService;
        this.xmlMapper = xmlMapper;
    }

    @JmsListener(destination = "BrokerResponseQueue")
    public void receivePaymentRequest(String xmlResponse) throws JsonProcessingException {
        logger.info("Broker and Fraud check response received");
        FraudCheckResponse response = xmlMapper.readValue(xmlResponse, FraudCheckResponse.class);
        paymentService.updatePaymentStatus(response);
    }
}
