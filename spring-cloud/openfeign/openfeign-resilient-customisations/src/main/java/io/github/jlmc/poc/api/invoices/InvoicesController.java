package io.github.jlmc.poc.api.invoices;

import io.github.jlmc.poc.api.invoices.outputs.InvoiceRepresentation;
import io.github.jlmc.poc.domain.invoices.boundary.InvoiceArchive;
import io.github.jlmc.poc.domain.invoices.entities.Invoice;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api/invoices")
public class InvoicesController {

    final InvoiceArchive invoiceArchive;

    public InvoicesController(InvoiceArchive invoiceArchive) {
        this.invoiceArchive = invoiceArchive;
    }

    @GetMapping(path = "/{number}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<InvoiceRepresentation> getInvoice(@PathVariable("number") String number) {
        Optional<Invoice> invoice = invoiceArchive.find(number);

        return invoice.map(InvoiceRepresentation::of)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
