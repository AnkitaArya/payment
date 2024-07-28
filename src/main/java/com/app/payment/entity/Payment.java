package com.app.payment.entity;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Builder(toBuilder = true)
@Table(name = "Payment", uniqueConstraints = {@UniqueConstraint(columnNames = {"payerName", "payerBank", "payeeName", "payeeBank", "amount", "executionDate"})})
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID transactionId;
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
