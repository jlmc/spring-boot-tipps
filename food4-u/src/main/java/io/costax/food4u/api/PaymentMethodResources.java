package io.costax.food4u.api;

import io.costax.food4u.domain.ResourceNotFoundException;
import io.costax.food4u.domain.model.PaymentMethod;
import io.costax.food4u.domain.repository.PaymentMethodRepository;
import io.costax.food4u.domain.services.PaymentMethodRegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/payment-methods")
public class PaymentMethodResources {

    @Autowired
    PaymentMethodRepository repository;

    @Autowired
    PaymentMethodRegistrationService paymentMethodRegistrationService;

    @GetMapping
    public List<PaymentMethod> list() {
        return repository.findAll(Sort.by("id"));
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{payment-method-id}")
    public PaymentMethod getById(@PathVariable("payment-method-id") Long id) {
        final PaymentMethod paymentMethod = repository
                .findById(id).orElseThrow(() -> ResourceNotFoundException.of(PaymentMethod.class, id));
        //repository.refresh(paymentMethod);
        return paymentMethod;
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody  PaymentMethod paymentMethod) {
        final PaymentMethod added = paymentMethodRegistrationService.add(paymentMethod);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest().path("/{id}")
                .buildAndExpand(added.getId())
                .toUri();

        return ResponseEntity.status(HttpStatus.CREATED)
                .location(location)
                .body(added);
    }
}
