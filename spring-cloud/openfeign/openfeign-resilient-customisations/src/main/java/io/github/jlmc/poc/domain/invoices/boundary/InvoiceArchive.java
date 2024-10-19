package io.github.jlmc.poc.domain.invoices.boundary;

import io.github.jlmc.poc.domain.invoices.control.FileNameExtractor;
import io.github.jlmc.poc.domain.invoices.entities.Invoice;
import io.github.jlmc.poc.domain.invoices.ports.OldInvoiceArchive;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
public class InvoiceArchive implements ApplicationRunner {
    private static final Logger LOGGER = LoggerFactory.getLogger(InvoiceArchive.class);

    private ConcurrentMap<String, Invoice> invoices = new ConcurrentHashMap<>();

    private final FileNameExtractor fileNameExtractor;
    private final Clock clock;
    private final OldInvoiceArchive oldInvoiceArchive;

    public InvoiceArchive(FileNameExtractor fileNameExtractor, Clock clock, OldInvoiceArchive oldInvoiceArchive) {
        this.fileNameExtractor = fileNameExtractor;
        this.clock = clock;
        this.oldInvoiceArchive = oldInvoiceArchive;
    }


    @Override
    public void run(ApplicationArguments args) throws Exception {
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();

        // Adjust the path to match your classpath directory
        Resource[] resources = resolver.getResources("classpath:/documents/invoices/*");

        for (Resource resource : resources) {

            LOGGER.info("LOADING {}", resource.getFilename());

            //String contentAsString = resource.getContentAsString(StandardCharsets.UTF_8);
            byte[] contentAsByteArray = resource.getContentAsByteArray();


            String filename = resource.getFilename();

            String invoiceNumber = fileNameExtractor.extract(filename);

            Invoice invoice = new Invoice(invoiceNumber, Instant.now(clock), BigDecimal.TEN, contentAsByteArray);

            invoices.put(invoice.number(), invoice);
        }
    }



    public Optional<Invoice> find(String number) {
        LOGGER.info("Find invoice by number {}", number);
        Optional<Invoice> invoice = Optional.ofNullable(invoices.get(number));

        if (invoice.isPresent()) {
            return invoice;
        }

        LOGGER.info("Invoice not found in the latest invoice repository try in the old test ones");
        return oldInvoiceArchive.find(number);

    }
}
