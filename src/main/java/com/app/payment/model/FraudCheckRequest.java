package com.app.payment.model;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class FraudCheckRequest {

    @NotNull
    private String transactionId;
    @NotNull
    private String payerName;
    @NotNull
    private String payerBank;
    @NotNull
    private String payerCountryCode;
    @NotNull
    private String payeeName;
    @NotNull
    private String payeeBank;
    @NotNull
    private String payeeCountryCode;
    private String paymentInstruction;

    @NotNull
    private Long amount;
    @NotNull
    private String currency;

}
