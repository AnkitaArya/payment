package com.app.payment.producer;

import com.app.payment.model.FraudCheckRequest;
import com.app.payment.model.FraudCheckResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

@Component
public class BrokerProducer {
    private static final Logger logger = LoggerFactory.getLogger(BrokerProducer.class);
    private JmsTemplate jmsTemplate;
    private XmlMapper xmlMapper;

    public BrokerProducer(JmsTemplate jmsTemplate, XmlMapper xmlMapper) {
        this.jmsTemplate = jmsTemplate;
        this.xmlMapper = xmlMapper;
    }

    public void convertAndSendForFraudCheck(FraudCheckRequest request, String queueName) {
        try {
            xmlMapper.registerModule(new JavaTimeModule());
            String xmlMessage = xmlMapper.writeValueAsString(request);
            jmsTemplate.convertAndSend(queueName, xmlMessage);
            logger.info("published message {} to queue {}", xmlMessage, queueName);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendForFraudCheck(String xmlMessage, String queueName) {
        jmsTemplate.convertAndSend(queueName, xmlMessage);
        logger.info("published message {} to queue {}", xmlMessage, queueName);
    }

    public void convertAndSendToPayment(FraudCheckResponse response, String brokerReponseQueue) {
        try {
            String xmlMessage = xmlMapper.writeValueAsString(response);
            jmsTemplate.convertAndSend(brokerReponseQueue, xmlMessage);
            logger.info("published message {} to queue {}", xmlMessage, brokerReponseQueue);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
