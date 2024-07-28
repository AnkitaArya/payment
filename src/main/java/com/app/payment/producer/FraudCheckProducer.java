package com.app.payment.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

@Component
public class FraudCheckProducer {
    private JmsTemplate jmsTemplate;
    private XmlMapper xmlMapper;

    public FraudCheckProducer(JmsTemplate jmsTemplate,XmlMapper xmlMapper) {
        this.jmsTemplate = jmsTemplate;
        this.xmlMapper = xmlMapper;
    }

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
