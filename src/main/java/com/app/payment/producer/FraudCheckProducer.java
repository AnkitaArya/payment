package com.app.payment.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

@Component
public class FraudCheckProducer {
    private static final Logger logger = LoggerFactory.getLogger(FraudCheckProducer.class);
    private JmsTemplate jmsTemplate;
    private XmlMapper xmlMapper;

    public FraudCheckProducer(JmsTemplate jmsTemplate, XmlMapper xmlMapper) {
        this.jmsTemplate = jmsTemplate;
        this.xmlMapper = xmlMapper;
    }

    public void sendMessage(Object object, String queueName) {
        try {
            String xmlMessage = xmlMapper.writeValueAsString(object);
            jmsTemplate.convertAndSend(queueName, xmlMessage);
            logger.info("Published fraud check response, payload {}", xmlMessage);
        } catch (JsonProcessingException e) {
            //TODO: exception handling
            throw new RuntimeException(e);
        }
    }
}
