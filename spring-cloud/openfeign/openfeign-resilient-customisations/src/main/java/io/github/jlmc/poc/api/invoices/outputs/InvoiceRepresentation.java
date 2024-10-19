package io.github.jlmc.poc.api.invoices.outputs;

import io.github.jlmc.poc.domain.invoices.entities.Invoice;

import java.time.Instant;

public record InvoiceRepresentation(
        String number,
        Instant date,
        byte[] fileContent
) {
    public static InvoiceRepresentation of(Invoice invoice) {
        return new InvoiceRepresentation(invoice.number(), invoice.date(), invoice.fileContent());
    }
}
