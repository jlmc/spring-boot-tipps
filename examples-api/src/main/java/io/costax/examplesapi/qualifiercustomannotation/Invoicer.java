package io.costax.examplesapi.qualifiercustomannotation;

import java.math.BigDecimal;

public interface Invoicer {

    BigDecimal invoice(String doc);
}
