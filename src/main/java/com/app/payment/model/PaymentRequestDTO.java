package com.app.payment.model;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class PaymentRequestDTO {
    @NotNull
    private String transactionId;
    @NotNull
    private String payerName;
    @NotNull
    private String payerBank;
    @NotNull
    @Pattern(regexp = "^[A-Z]{3}$", message = "Payer country code should be a valid ISO alpha-3 country code")
    private String payerCountryCode;
    @NotNull
    private String payeeName;
    @NotNull
    private String payeeBank;
    @NotNull
    @Pattern(regexp = "^[A-Z]{3}$", message = "Payee country code should be a valid ISO alpha-3 country code")
    private String payeeCountryCode;
    private String paymentInstruction;

    private LocalDate executionDate;

    @NotNull
    private Long amount;
    @NotNull
    @Pattern(regexp = "^[A-Z]{3}$", message = "Currency should be a valid ISO 4217 currency code")
    private String currency;

    @NotNull
    private LocalDateTime creationTimestamp;

}
