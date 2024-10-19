package io.github.jlmc.poc.adapters.invoices;

import feign.FeignException;
import io.github.jlmc.poc.adapters.invoices.control.ExodusClient;
import io.github.jlmc.poc.domain.invoices.entities.Invoice;
import io.github.jlmc.poc.domain.invoices.ports.OldInvoiceArchive;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ExodusOldInvoiceArchive implements OldInvoiceArchive {

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(ExodusOldInvoiceArchive.class);

    private final ExodusClient exodusClient;

    public ExodusOldInvoiceArchive(ExodusClient exodusClient) {
        this.exodusClient = exodusClient;
    }

    @Override
    public Optional<Invoice> find(String number) {

        try {
            Invoice invoice = exodusClient.getInvoice(number);

            return Optional.of(invoice);
        } catch (FeignException.NotFound e) {
            LOGGER.warn("The Exodus invoice with the number [{}] could not be found", number);
            return Optional.empty();
        }
    }
}
