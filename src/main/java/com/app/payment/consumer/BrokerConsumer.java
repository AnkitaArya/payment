package com.app.payment.consumer;


import com.app.payment.model.PaymentRequestDTO;
import com.app.payment.service.BrokerService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
public class BrokerConsumer {
    private static final Logger logger = LoggerFactory.getLogger(BrokerConsumer.class);


    private BrokerService brokerService;

    private XmlMapper xmlMapper;

    public BrokerConsumer(BrokerService brokerService,XmlMapper xmlMapper) {
        this.brokerService = brokerService;
        this.xmlMapper = xmlMapper;
    }

    @JmsListener(destination = "FraudCheckResponseQueueV1")
    public void receiveFraudCheckResponseV1(String xmlResponse) {
        logger.info("FraudDetectionResponse Received");
        brokerService.processFraudCheckResponse(xmlResponse, "api");
    }

    @JmsListener(destination = "FraudCheckResponseQueueV2")
    public void receiveFraudCheckResponseV2(String xmlResponse) {
        logger.info("FraudDetectionResponse Received");
        brokerService.processFraudCheckResponse(xmlResponse, "jms");
    }

    @JmsListener(destination = "BrokerRequestQueue")
    public void receivePaymentRequest(String xmlResponse) throws JsonProcessingException {
        logger.info("Payment Request Received");
        PaymentRequestDTO paymentRequestDTO = xmlMapper.readValue(xmlResponse, PaymentRequestDTO.class);
        brokerService.processPayment(paymentRequestDTO,"jms");
    }

}
