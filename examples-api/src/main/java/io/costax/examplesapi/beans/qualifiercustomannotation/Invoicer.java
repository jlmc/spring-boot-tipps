package io.costax.examplesapi.beans.qualifiercustomannotation;

import java.math.BigDecimal;

public interface Invoicer {

    BigDecimal invoice(String doc);
}
