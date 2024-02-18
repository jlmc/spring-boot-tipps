package io.github.jlmc.uof.api;

import io.github.jlmc.uof.api.representations.CreateReservationRequest;
import io.github.jlmc.uof.domain.fruits.commands.CreateOrderReservationCommand;
import io.github.jlmc.uof.domain.fruits.core.FruitQueryReaderService;
import io.github.jlmc.uof.domain.fruits.entities.Fruit;
import io.github.jlmc.uof.domain.fruits.entities.FruitFullDetailed;
import io.github.jlmc.uof.domain.fruits.entities.ReservationId;
import io.github.jlmc.uof.domain.fruits.ports.incoming.CreateOrderReservation;
import io.github.jlmc.uof.domain.fruits.ports.incoming.FruitFullDetailedReader;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@Validated
@RestController
@RequestMapping(path = "/api/fruits")
public class FruitsController {

    private final FruitFullDetailedReader fruitFullDetailedReader;
    private final FruitQueryReaderService fruitQueryReaderService;
    private final CreateOrderReservation createOrderReservation;

    public FruitsController(FruitFullDetailedReader fruitFullDetailedReader,
                            FruitQueryReaderService fruitQueryReaderService,
                            CreateOrderReservation createOrderReservation) {
        this.fruitFullDetailedReader = fruitFullDetailedReader;
        this.fruitQueryReaderService = fruitQueryReaderService;
        this.createOrderReservation = createOrderReservation;
    }

    @GetMapping
    public List<Fruit> getAll() {
        return fruitQueryReaderService.getAll();
    }

    @GetMapping(path = "/{id:\\d+}")
    public ResponseEntity<FruitFullDetailed> getById(@PathVariable String id) {
        return fruitFullDetailedReader.find(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/__page")
    public Page<Fruit> getPage(@PageableDefault Pageable pageable) {
        return fruitQueryReaderService.getPage(pageable);
    }

    @PostMapping("/reservations")
    public ResponseEntity<ReservationId> createReservation(@RequestBody @Validated CreateReservationRequest payload) {
        CreateOrderReservationCommand command = payload.toOrderReservationCommand();
        ReservationId reservationId = createOrderReservation.create(command);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest().path("/{id}")
                .buildAndExpand(reservationId.id())
                .toUri();

        return ResponseEntity.status(HttpStatus.CREATED)
                .location(location)
                .body(reservationId);
    }
}

