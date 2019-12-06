package io.costax.examplesapi.beans.qualifiercustomannotation;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@InvoiceType(Type.LOW)
public class InvoicerADSE implements Invoicer {

    @Override
    public BigDecimal invoice(final String doc) {
        return BigDecimal.ZERO;
    }
}
