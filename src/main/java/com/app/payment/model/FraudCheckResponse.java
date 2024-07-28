package com.app.payment.model;

import lombok.Getter;
import lombok.Setter;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class FraudCheckResponse {
    @NotNull
    private String transactionId;
    @NotNull
    private String status;

}
