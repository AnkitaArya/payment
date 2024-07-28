package com.app.payment.consumer;

import com.app.payment.model.FraudCheckResponse;
import com.app.payment.model.PaymentRequestDTO;
import com.app.payment.producer.FraudCheckProducer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
public class FraudCheckConsumer {

    private XmlMapper xmlMapper;
    FraudCheckProducer fraudCheckProducer;
    public FraudCheckConsumer(FraudCheckProducer fraudCheckProducer, XmlMapper xmlMapper) {
        this.fraudCheckProducer = fraudCheckProducer;
        this.xmlMapper = xmlMapper;
    }

    @JmsListener(destination = "FraudCheckRequestQueue")
    public void receiveAndValidateMessage(final String xmlMessage) throws JsonProcessingException {
        System.out.println("received XML message");
        PaymentRequestDTO messageData = xmlMapper.readValue(xmlMessage, PaymentRequestDTO.class);
        FraudCheckResponse responseDTO = new FraudCheckResponse();
        if (!messageData.getPayerName().equalsIgnoreCase("danger")) {
            responseDTO.setTransactionId(messageData.getTransactionId());
            responseDTO.setStatus("Approved");
        } else {
            responseDTO.setTransactionId(messageData.getTransactionId());
            responseDTO.setStatus("Rejected");
        }
        fraudCheckProducer.sendMessage(responseDTO, "FraudDetectionResponseQueue");
    }

}
