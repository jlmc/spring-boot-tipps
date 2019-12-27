package io.costax.food4u.api;


import com.fasterxml.jackson.databind.ObjectMapper;
import io.costax.food4u.api.assembler.restaurants.input.RestaurantInputRepresentationDisassembler;
import io.costax.food4u.api.assembler.restaurants.output.RestaurantOutputRepresentationAssembler;
import io.costax.food4u.api.model.restaurants.input.AddressInputRepresentation;
import io.costax.food4u.api.model.restaurants.input.CookerInputRepresentation;
import io.costax.food4u.api.model.restaurants.input.RestaurantInputRepresentation;
import io.costax.food4u.api.model.restaurants.output.RestaurantOutputRepresentation;
import io.costax.food4u.core.validation.ManualValidationException;
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
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.SmartValidator;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.lang.reflect.Field;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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
    private final SmartValidator validator;

    private final RestaurantOutputRepresentationAssembler assembler;
    private final RestaurantInputRepresentationDisassembler disassembler;

    public RestaurantResources(final RestaurantRepository restaurantRepository,
                               final RestaurantRegistrationService restaurantRegistrationService,
                               final ObjectMapper objectMapper,
                               final SmartValidator validator,
                               final RestaurantOutputRepresentationAssembler assembler,
                               final RestaurantInputRepresentationDisassembler disassembler) {
        this.restaurantRepository = restaurantRepository;
        this.restaurantRegistrationService = restaurantRegistrationService;
        this.objectMapper = objectMapper;
        this.validator = validator;
        this.assembler = assembler;
        this.disassembler = disassembler;
    }

    @GetMapping
    public List<RestaurantOutputRepresentation> list() {
        return restaurantRepository.findAll().stream().map(assembler::toRepresentation).collect(Collectors.toList());
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{restaurantId}")
    public RestaurantOutputRepresentation getById(@PathVariable("restaurantId") Long id) {
        return restaurantRepository
                .findById(id)
                .map(assembler::toRepresentation)
                .orElseThrow(() -> RestaurantNotFoundException.of(id));
    }

    @PostMapping
    public ResponseEntity<?> add(@RequestBody @Valid RestaurantInputRepresentation payload) {

        final Restaurant restaurant1 = disassembler.toDomainObject(payload);

        final Restaurant added = restaurantRegistrationService.add(restaurant1);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest().path("/{id}")
                .buildAndExpand(added.getId())
                .toUri();

        return ResponseEntity.status(HttpStatus.CREATED)
                .location(location)
                .body(assembler.toRepresentation(added));
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/{restaurantId}")
    public RestaurantOutputRepresentation update(@PathVariable("restaurantId") Long restaurantId,
                                                 @RequestBody @Valid RestaurantInputRepresentation payload) {

        //final Restaurant restaurant = disassembler.toDomainObject(payload);

        Restaurant restaurant = restaurantRepository
                .findById(restaurantId)
                .orElseThrow(() -> RestaurantNotFoundException.of(restaurantId));

        disassembler.copyProperties(payload, restaurant);


        final Restaurant updated = restaurantRegistrationService.update(restaurantId, restaurant);

        return assembler.toRepresentation(updated);
    }

    @PatchMapping("/{restaurantId}")
    public RestaurantOutputRepresentation partialUpdate(@PathVariable("restaurantId") Long restaurantId,
                                                        @RequestBody Map<String, Object> payload,
                                                        HttpServletRequest request) {
        final Restaurant current = restaurantRepository
                .findById(restaurantId)
                .orElseThrow(() -> RestaurantNotFoundException.of(restaurantId));

        final RestaurantInputRepresentation restaurantInputRepresentationFromTheExistentDomain = new RestaurantInputRepresentation();
        restaurantInputRepresentationFromTheExistentDomain.setName(current.getName());
        restaurantInputRepresentationFromTheExistentDomain.setTakeAwayTax(current.getTakeAwayTax());
        restaurantInputRepresentationFromTheExistentDomain.setCooker(Optional.ofNullable(current.getCooker()).map(c -> {
            final CookerInputRepresentation x = new CookerInputRepresentation();
            x.setId(c.getId());
            return x;
        }).orElse(null));
        restaurantInputRepresentationFromTheExistentDomain.setAddress(Optional.ofNullable(current.getAddress()).map(a -> {
            AddressInputRepresentation x = new AddressInputRepresentation();
            x.setCity(a.getCity());
            x.setStreet(a.getStreet());
            x.setZipCode(a.getZipCode());
            return x;

        }).orElse(null));

        final RestaurantInputRepresentation merged = merge(payload, restaurantInputRepresentationFromTheExistentDomain, request);
        validate(merged, "restaurant");

        return this.update(restaurantId, merged);
    }

    private void validate(RestaurantInputRepresentation restaurantInputRepresentation, String objectName) {
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(restaurantInputRepresentation, objectName);
        validator.validate(restaurantInputRepresentation, bindingResult);

        if (bindingResult.hasErrors()) {
            throw new ManualValidationException(bindingResult);
        }
    }

    private RestaurantInputRepresentation merge(Map<String, Object> payload, RestaurantInputRepresentation restaurantTarget, HttpServletRequest request) {

        try {
            //ObjectMapper objectMapper = new ObjectMapper();
            //objectMapper.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, true);
            //objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true);

            RestaurantInputRepresentation restaurantSource = objectMapper.convertValue(payload, RestaurantInputRepresentation.class);

            payload.forEach((propertyName, propertyValue) -> {
                final Field field = ReflectionUtils.findField(RestaurantInputRepresentation.class, propertyName);
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

    @PutMapping("/{restaurantId}/activation")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void ativar(@PathVariable Long restaurantId) {
        restaurantRegistrationService.activate(restaurantId);
    }

    @DeleteMapping("/{restaurantId}/inactivation")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void inactivate(@PathVariable Long restaurantId) {
        restaurantRegistrationService.inactivate(restaurantId);
    }
}
