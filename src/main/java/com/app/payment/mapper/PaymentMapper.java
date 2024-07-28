package com.app.payment.mapper;

import com.app.payment.entity.Payment;
import com.app.payment.model.FraudCheckRequest;
import com.app.payment.model.PaymentRequestDTO;

public class PaymentMapper {

    public static Payment convertToEntity(PaymentRequestDTO paymentDto) {
        Payment paymentEntity = new Payment();
        paymentEntity.setTransactionId(paymentDto.getTransactionId());
        paymentEntity.setPayerName(paymentDto.getPayerName());
        paymentEntity.setPayeeName(paymentDto.getPayeeName());
        paymentEntity.setAmount(Long.valueOf(paymentDto.getAmount()));
        paymentEntity.setCurrency(paymentDto.getCurrency());
        paymentEntity.setPayerCountryCode(paymentDto.getPayerCountryCode());
        paymentEntity.setPayeeCountryCode(paymentDto.getPayeeCountryCode());
        paymentEntity.setPayerBank(paymentDto.getPayerBank());
        paymentEntity.setPayeeBank(paymentDto.getPayeeBank());
        paymentEntity.setPaymentInstruction(paymentDto.getPaymentInstruction());
        paymentEntity.setCreationTimestamp(paymentDto.getCreationTimestamp());
        paymentEntity.setExecutionDate(paymentDto.getExecutionDate());
        paymentEntity.setStatus("Created");
        return paymentEntity;
    }

    public static FraudCheckRequest convertToFraudCheckDto(PaymentRequestDTO incomingDto){
        FraudCheckRequest fraudCheckRequest = new FraudCheckRequest();
        fraudCheckRequest.setAmount(incomingDto.getAmount());
        fraudCheckRequest.setTransactionId(incomingDto.getTransactionId());
        fraudCheckRequest.setCurrency(incomingDto.getCurrency());
        fraudCheckRequest.setPaymentInstruction(incomingDto.getPaymentInstruction());
        fraudCheckRequest.setPayeeBank(incomingDto.getPayeeBank());
        fraudCheckRequest.setPayeeName(incomingDto.getPayeeName());
        fraudCheckRequest.setPayerBank(incomingDto.getPayerBank());
        fraudCheckRequest.setPayerName(incomingDto.getPayerName());
        fraudCheckRequest.setPayerCountryCode(incomingDto.getPayerCountryCode());
        fraudCheckRequest.setPayeeCountryCode(incomingDto.getPayeeCountryCode());
        return fraudCheckRequest;
    }

}
