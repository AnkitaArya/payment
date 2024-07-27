package com.app.payment.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

@Component
public class FraudCheckProducer {
    @Autowired
    private JmsTemplate jmsTemplate;

    private final XmlMapper xmlMapper = new XmlMapper();

    public void sendMessage(Object object, String queueName) {
        try {
            // Convert object to XML string
            String xmlMessage = xmlMapper.writeValueAsString(object);

            // Send the message
            jmsTemplate.convertAndSend(queueName, xmlMessage);

            System.out.println("FraudCheckProducer: Sent XML message: " + xmlMessage);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
