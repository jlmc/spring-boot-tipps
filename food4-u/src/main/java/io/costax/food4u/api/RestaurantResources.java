package io.costax.food4u.api;


import com.fasterxml.jackson.databind.ObjectMapper;
import io.costax.food4u.domain.ResourceNotFoundException;
import io.costax.food4u.domain.model.Restaurant;
import io.costax.food4u.domain.repository.RestaurantRepository;
import io.costax.food4u.domain.services.RestaurantRegistrationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.lang.reflect.Field;
import java.net.URI;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(
        value = "/restaurants"
        /*,
        produces = {
                MediaType.APPLICATION_JSON_VALUE
        },
        consumes = {
                MediaType.APPLICATION_JSON_VALUE
        }*/)
public class RestaurantResources {

    private final RestaurantRepository restaurantRepository;
    private final RestaurantRegistrationService restaurantRegistrationService;

    public RestaurantResources(final RestaurantRepository restaurantRepository, final RestaurantRegistrationService restaurantRegistrationService) {
        this.restaurantRepository = restaurantRepository;
        this.restaurantRegistrationService = restaurantRegistrationService;
    }

    @GetMapping
    public List<Restaurant> list() {
        return restaurantRepository.findAll();
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{restaurantId}")
    public Restaurant getById(@PathVariable("restaurantId") Long id) {
        return restaurantRepository
                .findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of(Restaurant.class, id));
    }

    @PostMapping
    public ResponseEntity<?> add(@RequestBody Restaurant restaurant) {
        try {
            final Restaurant added = restaurantRegistrationService.add(restaurant);

            URI location = ServletUriComponentsBuilder
                    .fromCurrentRequest().path("/{id}")
                    .buildAndExpand(added.getId())
                    .toUri();

            return ResponseEntity.status(HttpStatus.CREATED)
                    .location(location)
                    .body(added);

        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .badRequest()
                    .header("X-reason", e.getMessage())
                    .body(e.getMessage());
        }
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/{restaurantId}")
    public Restaurant update(@PathVariable("restaurantId") Long restaurantId,
                                       @RequestBody Restaurant restaurant) {
        return restaurantRegistrationService.update(restaurantId, restaurant);
    }

    @PatchMapping("/{restaurantId}")
    public Restaurant partialUpdate(@PathVariable("restaurantId") Long restaurantId,
                                    @RequestBody Map<String, Object> payload) {
        final Restaurant current = restaurantRepository
                .findById(restaurantId)
                .orElseThrow(() -> ResourceNotFoundException.of(Restaurant.class, restaurantId));

        final Restaurant merged = merge(payload, current);

        return this.update(restaurantId, merged);
    }

    private Restaurant merge(Map<String, Object> payload, Restaurant restaurantTarget) {
        ObjectMapper objectMapper = new ObjectMapper();

        Restaurant restaurantSource = objectMapper.convertValue(payload, Restaurant.class);

        payload.forEach((propertyName, propertyValue) -> {
            final Field field = ReflectionUtils.findField(Restaurant.class, propertyName);
            field.setAccessible(true);
            final Object value = ReflectionUtils.getField(field, restaurantSource);
            ReflectionUtils.setField(field, restaurantTarget, value);
        });

        return restaurantTarget;
    }
}
