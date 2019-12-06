package io.costax.examplesapi.qualifiercustomannotation;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Optional;

@Component
@InvoiceType(Type.HIGH)
public class InvoicerMedis implements Invoicer {

    public static final BigDecimal DEFAULT_TAX = new BigDecimal("1.23");

    @Override
    public BigDecimal invoice(final String doc) {
        return Optional.ofNullable(doc)
                .map(String::length)
                .map(i -> DEFAULT_TAX.multiply(BigDecimal.valueOf((long)i)))
                .orElse(DEFAULT_TAX);
    }
}
