package com.app.payment.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Builder(toBuilder = true)
@Table(name = "Payment")
public class Payment {
    @Id
    private String transactionId;
    private String payerName;
    private String payerBank;
    private String payerCountryCode;
    private String payeeName;
    private String payeeBank;
    private String payeeCountryCode;
    private String paymentInstruction;
    private Long amount;
    private String currency;
    private LocalDateTime creationTimestamp;
    private String status;
    private LocalDate executionDate;


}
