package io.costax.examplesapi.qualifiercustomannotation;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Currency;
import java.util.Locale;

@Controller
@RequestMapping(value = "/qualifier-custom-annotation")
public class QualifierCustomAnnotationResource {

    @Autowired
    @InvoiceType(Type.HIGH)
    Invoicer invoicer;

    private DecimalFormat decimalFormat;

    @PostConstruct
    public void initialize() {
        final Locale locale = new Locale("pt", "PT");
        final DecimalFormatSymbols decimalFormatSymbols = DecimalFormatSymbols.getInstance(locale);
        decimalFormat = new DecimalFormat("#,###.00 ¤", decimalFormatSymbols);
        decimalFormat.setRoundingMode(RoundingMode.HALF_UP);
        //decimalFormat.setRoundingMode(RoundingMode.HALF_EVEN);
        decimalFormat.setCurrency(Currency.getInstance(locale));
    }

    @GetMapping
    public ResponseEntity<String> hello() {

        final BigDecimal invoice = invoicer.invoice("2.19. Custom annotation bean disambiguation");

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(decimalFormat.format(invoice));
    }
}
