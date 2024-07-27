package com.app.payment;

import org.springframework.stereotype.Component;

import java.util.Currency;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class Validator {
    private static final Set<String> ISO_COUNTRY_CODES = Set.of(Locale.getISOCountries())
            .stream()
            .map(code -> new Locale("", code).getISO3Country())
            .collect(Collectors.toSet());

    private static final Set<String> ISO_CURRENCY_CODES = Currency.getAvailableCurrencies()
            .stream()
            .map(Currency::getCurrencyCode)
            .collect(Collectors.toSet());

    public boolean isValidCountryCode(String code) {
        return ISO_COUNTRY_CODES.contains(code);
    }

    public boolean isValidCurrencyCode(String code) {
        return ISO_CURRENCY_CODES.contains(code);
    }
}
