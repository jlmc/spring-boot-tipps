package io.github.jlmc.poc.domain.invoices.ports;

import io.github.jlmc.poc.domain.invoices.entities.Invoice;

import java.util.Optional;

public interface OldInvoiceArchive {
    Optional<Invoice> find(String number);
}
