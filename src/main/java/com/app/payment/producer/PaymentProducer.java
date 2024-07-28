package com.app.payment.producer;

import com.app.payment.model.PaymentRequestDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

@Component
public class PaymentProducer {
    private static final Logger logger = LoggerFactory.getLogger(PaymentProducer.class);
    private JmsTemplate jmsTemplate;
    private XmlMapper xmlMapper;

    public PaymentProducer(JmsTemplate jmsTemplate, XmlMapper xmlMapper) {
        this.jmsTemplate = jmsTemplate;
        this.xmlMapper = xmlMapper;
    }

    public void convertAndSend(PaymentRequestDTO object, String queueName){
        try {
            String xmlMessage = xmlMapper.writeValueAsString(object);
            jmsTemplate.convertAndSend(queueName, xmlMessage);
            logger.info("published message {} to queue {}", xmlMessage, queueName);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
