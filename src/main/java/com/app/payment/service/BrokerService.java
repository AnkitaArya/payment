package com.app.payment.service;

import com.app.payment.model.PaymentRequestDTO;
import com.app.payment.model.PaymentResponseDTO;
import com.app.payment.producer.BrokerProducer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class BrokerService {

    @Autowired
    BrokerProducer brokerProducer;
    private final XmlMapper xmlMapper = new XmlMapper();
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private static final Logger logger = LoggerFactory.getLogger(BrokerService.class);

    public void processPayment(PaymentRequestDTO requestDto) {
        //send the request to fraud check
        brokerProducer.convertAndSend(requestDto, "FraudCheckRequestQueue");
    }

    public void processFraudCheckResponse(String xmlResponse) {
        try {
            logger.info("Fraud check response received " + xmlResponse);
            PaymentResponseDTO response = xmlMapper.readValue(xmlResponse, PaymentResponseDTO.class);
            updatePaymentStatus(response);
            logger.info("Response received for transaction id: {} ", response.getTransactionId());
        } catch (Exception e) {
            logger.error("Error processing fraud check response: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private HttpResponse<String> updatePaymentStatus(PaymentResponseDTO fraudCheckResponse) throws IOException, InterruptedException {
        String url = "http://localhost:9095/api/v1/payment/engine/status";

        ObjectMapper mapper = new JsonMapper();
        String requestBody = mapper.writeValueAsString(fraudCheckResponse);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();
        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }
}
