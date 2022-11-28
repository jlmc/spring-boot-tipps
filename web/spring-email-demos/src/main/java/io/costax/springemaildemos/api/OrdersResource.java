package io.costax.springemaildemos.api;

import io.costax.springemaildemos.domain.orders.boundary.OrderRegistationService;
import io.costax.springemaildemos.domain.orders.control.OrderRepository;
import io.costax.springemaildemos.domain.orders.entity.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/orders")
public class OrdersResource {

    @Autowired
    OrderRegistationService service;
    @Autowired
    OrderRepository repository;

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Order order,
                                    UriComponentsBuilder uriComponentsBuilder) {
        final Order created = service.create(order);

        final URI uri = uriComponentsBuilder.path("{id}").build(created.getId());

        return ResponseEntity.created(uri).body(created);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{id}")
    public ResponseEntity<Order> getById(@PathVariable("id") Long id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
