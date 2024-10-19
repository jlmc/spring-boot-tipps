package io.github.jlmc.poc.adapters.invoices.control;

import io.github.jlmc.poc.domain.invoices.entities.Invoice;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "exodus-service-client", configuration = ExodusClientConfiguration.class)
public interface ExodusClient {

    @GetMapping(path = "/exodus/api/invoices/{number}", produces = MediaType.APPLICATION_JSON_VALUE)
    Invoice getInvoice(@PathVariable("number") String number);
}
