package com.app.payment.model;

import lombok.Getter;
import lombok.Setter;
import org.antlr.v4.runtime.misc.NotNull;

@Getter
@Setter
public class PaymentResponseDTO {
    @NotNull
    private String transactionId;
    @NotNull
    private String status;

}
