package io.github.jlmc.doc4poc.service.api;

import io.github.jlmc.doc4poc.service.infrastructure.messaging.OrderChanged;
import io.github.jlmc.doc4poc.service.infrastructure.messaging.ReportChangesPublisher;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping(value = "/api/orders", produces = {MediaType.APPLICATION_JSON_VALUE})
public class OrdersController {

    private final ReportChangesPublisher reportChangesPublisher;

    public OrdersController(ReportChangesPublisher reportChangesPublisher) {
        this.reportChangesPublisher = reportChangesPublisher;
    }

    @PostMapping(path = "/{orderId}/{status}")
    public ResponseEntity<String> change(@PathVariable(name = "orderId") String orderId,
                                         @PathVariable(name = "status") String status) {

        OrderChanged dto =
                OrderChanged.builder()
                        .code(orderId)
                        .status(status)
                        .build();

        this.reportChangesPublisher.publish(dto);

        return ResponseEntity.ok("OKAY");
    }
}
