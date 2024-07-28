package com.app.payment.service;

import com.app.payment.mapper.PaymentMapper;
import com.app.payment.model.FraudCheckRequest;
import com.app.payment.model.FraudCheckResponse;
import com.app.payment.model.PaymentRequestDTO;
import com.app.payment.producer.BrokerProducer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class BrokerService {
    private static final Logger logger = LoggerFactory.getLogger(BrokerService.class);

    @Value("${jms.queuename.fraudcheck.request.v1}")
    private String fraudCheckRequestQueueV1;

    @Value("${jms.queuename.fraudcheck.request.v2}")
    private String fraudCheckRequestQueueV2;
    @Value("${jms.queuename.broker.response}")
    private String brokerReponseQueue;

    private XmlMapper xmlMapper;
    private HttpClient httpClient;
    private ObjectMapper objectMapper;
    BrokerProducer brokerProducer;

    public BrokerService(BrokerProducer brokerProducer, HttpClient httpClient, ObjectMapper objectMapper, XmlMapper xmlMapper) {
        this.brokerProducer = brokerProducer;
        this.httpClient = httpClient;
        this.objectMapper = objectMapper;
        this.xmlMapper = xmlMapper;
    }



    public void processPayment(PaymentRequestDTO requestDto, String mode) {
        FraudCheckRequest fraudCheckRequest = PaymentMapper.convertToFraudCheckDto(requestDto);
        if(mode.equals("api")){
            brokerProducer.convertAndSendForFraudCheck(fraudCheckRequest, fraudCheckRequestQueueV1);
        } else
        brokerProducer.convertAndSendForFraudCheck(fraudCheckRequest, fraudCheckRequestQueueV2);
    }

    public void processFraudCheckResponse(String xmlResponse, String producerMode) {
        try {
            FraudCheckResponse response = xmlMapper.readValue(xmlResponse, FraudCheckResponse.class);
            if (producerMode.equals("api")){
                updatePaymentStatusV1(response);
            } else {
                updatePaymentStatusV2(response);
            }

            logger.info("Response received for transaction id: {} ", response.getTransactionId());
        } catch (Exception e) {
            logger.error("Error processing fraud check response: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void updatePaymentStatusV2(FraudCheckResponse response) {
        brokerProducer.convertAndSendForPaymentResponse(response, brokerReponseQueue);
    }


    private HttpResponse<String> updatePaymentStatusV1(FraudCheckResponse fraudCheckResponse) throws IOException, InterruptedException {
        String url = "http://localhost:9095/api/v1/payment";

        ObjectMapper mapper = new JsonMapper();
        String requestBody = mapper.writeValueAsString(fraudCheckResponse);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();
        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }
}
