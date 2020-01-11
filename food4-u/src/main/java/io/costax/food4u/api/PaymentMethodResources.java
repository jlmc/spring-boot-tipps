package io.costax.food4u.api;

import io.costax.food4u.api.assembler.paymentmethods.input.PaymentMethodInputRepresentationDisassembler;
import io.costax.food4u.api.assembler.paymentmethods.output.PaymentMethodOutputRepresentationAssembler;
import io.costax.food4u.api.model.paymentmethods.input.PaymentMethodInputRepresentation;
import io.costax.food4u.api.model.paymentmethods.output.PaymentMethodOutputRepresentation;
import io.costax.food4u.domain.exceptions.ResourceNotFoundException;
import io.costax.food4u.domain.model.PaymentMethod;
import io.costax.food4u.domain.repository.PaymentMethodRepository;
import io.costax.food4u.domain.services.PaymentMethodRegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/payment-methods")
public class PaymentMethodResources {

    @Autowired
    PaymentMethodRepository repository;

    @Autowired
    PaymentMethodRegistrationService paymentMethodRegistrationService;

    @Autowired
    PaymentMethodInputRepresentationDisassembler disassembler;

    @Autowired
    PaymentMethodOutputRepresentationAssembler assembler;

    @GetMapping
    public ResponseEntity<List<PaymentMethodOutputRepresentation>> list() {
        final List<PaymentMethod> paymentMethods = repository.findAll(Sort.by("id"));
        final List<PaymentMethodOutputRepresentation> paymentMethodOutputRepresentations = assembler.toListOfRepresentations(paymentMethods);

        return ResponseEntity
                .ok()
                //header Cache-Control: max-age=10 segundo
                .cacheControl(CacheControl.maxAge(10, TimeUnit.SECONDS))
                .body(paymentMethodOutputRepresentations);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{payment-method-id}")
    public ResponseEntity<PaymentMethodOutputRepresentation> getById(@PathVariable("payment-method-id") Long id) {
        //repository.refresh(paymentMethod);
        final PaymentMethodOutputRepresentation paymentMethodOutputRepresentation = repository
                .findById(id)
                .map(assembler::toRepresentation)
                .orElseThrow(() -> new ResourceNotFoundException(PaymentMethod.class, id));

        return ResponseEntity
                .ok()
                .cacheControl(CacheControl.maxAge(10, TimeUnit.SECONDS))
                //.cacheControl(CacheControl.maxAge(10, TimeUnit.SECONDS).cachePrivate())
                //.cacheControl(CacheControl.maxAge(10, TimeUnit.SECONDS).cachePublic())
                //.cacheControl(CacheControl.noCache())
                //.cacheControl(CacheControl.noStore())
                .body(paymentMethodOutputRepresentation);
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody PaymentMethodInputRepresentation payload) {

        final PaymentMethod paymentMethod1 = disassembler.toDomainObject(payload);
        final PaymentMethod added = paymentMethodRegistrationService.add(paymentMethod1);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest().path("/{id}")
                .buildAndExpand(added.getId())
                .toUri();

        return ResponseEntity.status(HttpStatus.CREATED)
                .location(location)
                .body(added);
    }
}
