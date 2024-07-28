package com.app.payment.service;

import com.app.payment.Validator;
import com.app.payment.entity.Payment;
import com.app.payment.mapper.PaymentMapper;
import com.app.payment.model.FraudCheckResponse;
import com.app.payment.model.PaymentRequestDTO;
import com.app.payment.repository.PaymentRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;


@Service
public class PaymentService {
    private static final Logger logger = LoggerFactory.getLogger(PaymentService.class);

    private final Validator validator;
    private PaymentRepository paymentRepository;
    private HttpClient httpClient;
    private ObjectMapper objectMapper;

    public PaymentService(Validator validator, PaymentRepository paymentRepository, HttpClient httpClient, ObjectMapper objectMapper) {
        this.validator = validator;
        this.paymentRepository = paymentRepository;
        this.httpClient = httpClient;
        this.objectMapper = objectMapper;
    }

    public String processPayment(PaymentRequestDTO requestDto) throws IOException, InterruptedException {


        if (!validatePayment(requestDto)) {
            return "Invalid payment details";
        } else {
            Payment payment = PaymentMapper.convertToEntity(requestDto);
            paymentRepository.save(payment);
            HttpResponse<String> response = sendToBrokerService(requestDto);
            if (response.statusCode() == 200) {
                logger.info("Payment submitted for fraud check");
            } else {
                logger.error("Payment failed while submitted for fraud check");
                //Todo throw error
            }
            return requestDto.getTransactionId();
        }

    }

    public Payment getPaymentStatus(String transactionId) {
        return paymentRepository.findByTransactionId(transactionId).get();
    }

    public Optional<Payment> updatePaymentStatus(FraudCheckResponse response) {
        logger.info("Updating payment status for transactionId: {}", response.getTransactionId());
        Optional<Payment> paymentOptional = paymentRepository.findByTransactionId(response.getTransactionId());
        if (paymentOptional.isPresent()) {
            Payment payment = paymentOptional.get();
            payment.setStatus(response.getStatus());
            paymentRepository.save(payment);
            return Optional.of(payment);
        }
        return Optional.empty();
    }

    private Boolean validatePayment(PaymentRequestDTO paymentDto) {
        if (!validator.isValidCountryCode(paymentDto.getPayerCountryCode())) {
            return false;
        }

        if (!validator.isValidCountryCode(paymentDto.getPayeeCountryCode())) {
            return false;
        }

        if (!validator.isValidCurrencyCode(paymentDto.getCurrency())) {
            return false;
        }

        return true;
    }



    private HttpResponse<String> sendToBrokerService(PaymentRequestDTO paymentRequestDTO) throws IOException, InterruptedException {
        //String url = hostname+port+endpoint;
        String url = "http://localhost:9095/api/v1/broker";
        String requestBody = objectMapper.writeValueAsString(paymentRequestDTO);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();
        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());

    }

}
