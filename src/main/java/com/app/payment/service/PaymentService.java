package com.app.payment.service;

import com.app.payment.exception.PaymentException;
import com.app.payment.exception.ValidationException;
import com.app.payment.model.FraudCheckRequest;
import com.app.payment.producer.PaymentProducer;
import com.app.payment.validate.PaymentValidator;
import com.app.payment.entity.Payment;
import com.app.payment.mapper.PaymentMapper;
import com.app.payment.model.FraudCheckResponse;
import com.app.payment.model.PaymentRequestDTO;
import com.app.payment.repository.PaymentRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.validation.ConstraintViolationException;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;
import java.util.UUID;


@Service
public class PaymentService {
    private static final Logger logger = LoggerFactory.getLogger(PaymentService.class);

    @Value("${jms.queuename.broker.request}")
    private String brokerRequestQueue;

    private final PaymentValidator paymentValidator;
    private PaymentRepository paymentRepository;
    private HttpClient httpClient;
    private ObjectMapper objectMapper;
    private PaymentProducer paymentProducer;

    public PaymentService(PaymentValidator paymentValidator, PaymentRepository paymentRepository, HttpClient httpClient, ObjectMapper objectMapper, PaymentProducer paymentProducer) {
        this.paymentValidator = paymentValidator;
        this.paymentRepository = paymentRepository;
        this.httpClient = httpClient;
        this.objectMapper = objectMapper;
        this.paymentProducer = paymentProducer;
    }

    public String processPayment(PaymentRequestDTO requestDto) throws IOException, InterruptedException {

        if (!validatePayment(requestDto)) {
            throw new ValidationException("Validation failed for the input values", HttpStatus.BAD_REQUEST.value());
        } else {
            try{
                Payment payment = PaymentMapper.convertToEntity(requestDto);
                paymentRepository.save(payment);
                requestDto.setTransactionId(payment.getTransactionId().toString());
                FraudCheckRequest fraudCheckRequest = PaymentMapper.convertToFraudCheckDto(requestDto, "api");
                HttpResponse<String> response = sendToBrokerService(fraudCheckRequest);
                if (response.statusCode() == 200) {
                    logger.info("Payment submitted for fraud check");
                } else {
                    logger.error("Payment failed while submitted for fraud check");
                    throw new PaymentException("Payment failed while submitted for fraud check", HttpStatus.INTERNAL_SERVER_ERROR.value());
                }
            }catch (DataIntegrityViolationException ex){
                logger.error("Duplicate payment received!");
                throw new PaymentException("Duplicate payment received!", HttpStatus.CONFLICT.value());
            }

            return requestDto.getTransactionId();
        }

    }
    public String processPaymentV2(PaymentRequestDTO requestDto){
        if (!validatePayment(requestDto)) {
            throw new ValidationException("Validation failed for the input values", HttpStatus.BAD_REQUEST.value());
        } else {
            Payment payment = PaymentMapper.convertToEntity(requestDto);
            try {
                paymentRepository.save(payment);
                requestDto.setTransactionId(payment.getTransactionId().toString());
                FraudCheckRequest fraudCheckRequest = PaymentMapper.convertToFraudCheckDto(requestDto, "jms");
                paymentProducer.convertAndSend(fraudCheckRequest, brokerRequestQueue);
                return requestDto.getTransactionId();
            }catch (DataIntegrityViolationException ex){
                logger.error("Duplicate payment received!");
                throw new PaymentException("Duplicate payment received!", HttpStatus.CONFLICT.value());
            }catch (Exception ex){
                logger.error("Exception while sending request to broker queue");
                throw new PaymentException("Payment failed while submitted for fraud check", HttpStatus.INTERNAL_SERVER_ERROR.value());
            }
        }
    }

    public Payment getPaymentStatus(UUID transactionId) {
        return paymentRepository.findByTransactionId(transactionId).get();
    }

    public Payment updatePaymentStatus(FraudCheckResponse response) {
        logger.info("Updating payment status for transactionId: {}", response.getTransactionId());
        Optional<Payment> paymentOptional = paymentRepository.findByTransactionId(UUID.fromString(response.getTransactionId()));
        if (paymentOptional.isPresent()) {
            Payment payment = paymentOptional.get();
            payment.setStatus(response.getStatus());
            paymentRepository.save(payment);
            return payment;
        }
        else {
            logger.error("No payment found for transaction Id {}", response.getTransactionId());
            throw new PaymentException("No payment found for transaction Id {} " + response.getTransactionId(), HttpStatus.NOT_FOUND.value());
        }

    }

    private Boolean validatePayment(PaymentRequestDTO paymentDto) {
        if (!paymentValidator.isValidCountryCode(paymentDto.getPayerCountryCode())) {
            return false;
        }

        if (!paymentValidator.isValidCountryCode(paymentDto.getPayeeCountryCode())) {
            return false;
        }

        if (!paymentValidator.isValidCurrencyCode(paymentDto.getCurrency())) {
            return false;
        }

        return true;
    }



    private HttpResponse<String> sendToBrokerService(FraudCheckRequest fraudCheckRequest) throws IOException, InterruptedException {
        String url = "http://localhost:9095/api/v1/broker";
        String requestBody = objectMapper.writeValueAsString(fraudCheckRequest);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();
        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());

    }

}
