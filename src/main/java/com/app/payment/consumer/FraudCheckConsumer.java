package com.app.payment.consumer;

import com.app.payment.model.PaymentRequestDTO;
import com.app.payment.model.PaymentResponseDTO;
import com.app.payment.producer.FraudCheckProducer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
public class FraudCheckConsumer {

    private final XmlMapper xmlMapper = new XmlMapper();
    @Autowired
    FraudCheckProducer fraudCheckProducer;

    @JmsListener(destination = "FraudCheckRequestQueue")
    public void receiveAndValidateMessage(final String xmlMessage) throws JsonProcessingException {
        System.out.println("received XML message");
        PaymentRequestDTO messageData = xmlMapper.readValue(xmlMessage, PaymentRequestDTO.class);
        PaymentResponseDTO responseDTO = new PaymentResponseDTO();
        if (!messageData.getPayerName().equalsIgnoreCase("danger")) {
            responseDTO.setTransactionId(messageData.getTransactionId());
            responseDTO.setStatus("Approved");
        } else {
            responseDTO.setTransactionId(messageData.getTransactionId());
            responseDTO.setStatus("Rejected");
        }
        fraudCheckProducer.sendMessage(responseDTO, "FraudDetectionResponseQueue");
        // return responseDTO;
    }

}
