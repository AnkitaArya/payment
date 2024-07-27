package com.app.payment.service;

import com.app.payment.Validator;
import com.app.payment.model.PaymentRequestDTO;
import com.app.payment.model.PaymentResponseDTO;
import com.app.payment.entity.Payment;
import com.app.payment.repository.PaymentRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

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
    private final HttpClient httpClient = HttpClient.newHttpClient();

    public PaymentService(Validator validator, PaymentRepository paymentRepository) {
        this.validator = validator;
        this.paymentRepository = paymentRepository;
    }

    public String processPayment(PaymentRequestDTO requestDto) throws IOException, InterruptedException {


        if (!validatePayment(requestDto)) {
            return "Invalid payment details";
        } else {
            Payment payment = convertToEntity(requestDto);
            HttpResponse<String> response = sendToBrokerService(requestDto);

            if (response.statusCode() == 200) {
                logger.info("Payment submitted for fraud check");
                payment.setStatus("Submitted for fraud check");
                paymentRepository.save(payment);
            } else {
                logger.error("Payment submission for fraud check failed");
                payment.setStatus("Failed");
                paymentRepository.save(payment);
            }
            return requestDto.getTransactionId();
        }

    }

    public Payment getPaymentStatus(@RequestParam String transactionId) {

        return paymentRepository.findByTransactionId(transactionId).get();
    }


    public Payment getPayment(String paymentId) {
        return paymentRepository.findByTransactionId(paymentId).get();
    }

    public Optional<Payment> updatePaymentStatus(PaymentResponseDTO response) {
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

    private Payment convertToEntity(PaymentRequestDTO paymentDto) {
        Payment paymentEntity = new Payment();
        paymentEntity.setTransactionId(paymentDto.getTransactionId());
        paymentEntity.setPayerName(paymentDto.getPayerName());
        paymentEntity.setAmount(Long.valueOf(paymentDto.getAmount()));
        paymentEntity.setCurrency(paymentDto.getCurrency());
        paymentEntity.setPayerCountryCode(paymentDto.getPayerCountryCode());
        paymentEntity.setPayeeCountryCode(paymentDto.getPayeeCountryCode());
        paymentEntity.setPayerBank(paymentDto.getPayerBank());
        paymentEntity.setPayeeBank(paymentDto.getPayeeBank());
        paymentEntity.setPaymentInstruction(paymentDto.getPaymentInstruction());
        return paymentEntity;
    }

    private HttpResponse<String> sendToBrokerService(PaymentRequestDTO paymentRequestDTO) throws IOException, InterruptedException {
        String url = "http://localhost:9095/api/v1/broker/process/request/data";

        ObjectMapper mapper = new JsonMapper();
        String requestBody = mapper.writeValueAsString(paymentRequestDTO);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();
        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());

    }

}
