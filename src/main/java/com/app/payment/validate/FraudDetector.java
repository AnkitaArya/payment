package com.app.payment.validate;

import com.app.payment.model.FraudCheckRequest;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class FraudDetector {

    private static final List<String> BLACKLISTED_NAMES= List.of("Mark Imaginary", "Goving Real", "Shakil Maybe", "Chang Imagine");
    private static final List<String> BLACKLISTED_COUNTRIES= List.of("CUB", "IRQ", "IRN", "PRK", "SDN", "SYR");
    private static final List<String> BLACKLISTED_BANKS= List.of("BANK OF KUNLUN","KARAMAY CITY COMMERCIAL BANK");
    private static final List<String> BLACKLISTED_INSTRUCTIONS= List.of("Artillery Procurement", "Lethal Checmicals Payment");

    public static Boolean detectFraud(FraudCheckRequest request){
        if(BLACKLISTED_NAMES.contains(request.getPayerName().trim()) || BLACKLISTED_NAMES.contains(request.getPayeeName().trim())){
            return Boolean.TRUE;
        }
        if(BLACKLISTED_COUNTRIES.contains(request.getPayerCountryCode().trim()) || BLACKLISTED_COUNTRIES.contains(request.getPayeeCountryCode().trim())){
            return Boolean.TRUE;
        }
        if(BLACKLISTED_BANKS.contains(request.getPayerBank().trim()) || BLACKLISTED_BANKS.contains(request.getPayeeBank().trim())){
            return Boolean.TRUE;
        }
        if(BLACKLISTED_INSTRUCTIONS.contains(request.getPaymentInstruction().trim())){
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }



}
