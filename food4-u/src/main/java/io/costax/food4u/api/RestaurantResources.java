package io.costax.food4u.api;


import com.fasterxml.jackson.databind.ObjectMapper;
import io.costax.food4u.domain.exceptions.RestaurantNotFoundException;
import io.costax.food4u.domain.model.Restaurant;
import io.costax.food4u.domain.repository.RestaurantRepository;
import io.costax.food4u.domain.services.RestaurantRegistrationService;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
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
    private final ObjectMapper objectMapper;

    public RestaurantResources(final RestaurantRepository restaurantRepository,
                               final RestaurantRegistrationService restaurantRegistrationService,
                               final ObjectMapper objectMapper) {
        this.restaurantRepository = restaurantRepository;
        this.restaurantRegistrationService = restaurantRegistrationService;
        this.objectMapper = objectMapper;
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
                .orElseThrow(() -> RestaurantNotFoundException.of(id));
    }

    @PostMapping
    public ResponseEntity<?> add(@RequestBody @Valid Restaurant restaurant) {
        final Restaurant added = restaurantRegistrationService.add(restaurant);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest().path("/{id}")
                .buildAndExpand(added.getId())
                .toUri();

        return ResponseEntity.status(HttpStatus.CREATED)
                .location(location)
                .body(added);
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/{restaurantId}")
    public Restaurant update(@PathVariable("restaurantId") Long restaurantId,
                             @RequestBody Restaurant restaurant) {
        return restaurantRegistrationService.update(restaurantId, restaurant);
    }

    @PatchMapping("/{restaurantId}")
    public Restaurant partialUpdate(@PathVariable("restaurantId") Long restaurantId,
                                    @RequestBody Map<String, Object> payload,
                                    HttpServletRequest request) {
        final Restaurant current = restaurantRepository
                .findById(restaurantId)
                .orElseThrow(() -> RestaurantNotFoundException.of(restaurantId));

        final Restaurant merged = merge(payload, current, request);

        return this.update(restaurantId, merged);
    }

    private Restaurant merge(Map<String, Object> payload, Restaurant restaurantTarget, HttpServletRequest request) {

        try {
            //ObjectMapper objectMapper = new ObjectMapper();
            //objectMapper.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, true);
            //objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true);

            Restaurant restaurantSource = objectMapper.convertValue(payload, Restaurant.class);

            payload.forEach((propertyName, propertyValue) -> {
                final Field field = ReflectionUtils.findField(Restaurant.class, propertyName);
                field.setAccessible(true);
                final Object value = ReflectionUtils.getField(field, restaurantSource);
                ReflectionUtils.setField(field, restaurantTarget, value);
            });

            return restaurantTarget;
        } catch (IllegalArgumentException e) {

            // throw a exception that can be handler by the ApiExceptionHandler

            Throwable rootCause = ExceptionUtils.getRootCause(e);
            ServletServerHttpRequest serverHttpRequest = new ServletServerHttpRequest(request);
            throw new HttpMessageNotReadableException(e.getMessage(), rootCause, serverHttpRequest);
        }
    }
}
