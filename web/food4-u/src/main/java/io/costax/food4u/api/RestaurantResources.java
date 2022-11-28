package io.costax.food4u.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.costax.food4u.api.assembler.paymentmethods.output.PaymentMethodOutputRepresentationModelAssembler;
import io.costax.food4u.api.assembler.restaurants.input.RestaurantInputRepresentationDisassembler;
import io.costax.food4u.api.assembler.restaurants.output.RestaurantOutputRepresentationModelAssembler;
import io.costax.food4u.api.model.paymentmethods.output.PaymentMethodOutputRepresentation;
import io.costax.food4u.api.model.restaurants.input.AddressInputRepresentation;
import io.costax.food4u.api.model.restaurants.input.CookerInputRepresentation;
import io.costax.food4u.api.model.restaurants.input.RestaurantInputRepresentation;
import io.costax.food4u.api.model.restaurants.output.RestaurantOutputRepresentation;
import io.costax.food4u.api.openapi.controllers.RestaurantResourcesOpenApi;
import io.costax.food4u.core.validation.ManualValidationException;
import io.costax.food4u.domain.exceptions.RestaurantNotFoundException;
import io.costax.food4u.domain.model.Restaurant;
import io.costax.food4u.domain.repository.RestaurantRepository;
import io.costax.food4u.domain.services.RestaurantRegistrationService;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.util.ReflectionUtils;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.SmartValidator;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping(
        path = "/restaurants",
        produces = MediaType.APPLICATION_JSON_VALUE
)
public class RestaurantResources implements RestaurantResourcesOpenApi {

    private final RestaurantRepository restaurantRepository;
    private final RestaurantRegistrationService restaurantRegistrationService;
    private final ObjectMapper objectMapper;
    private final SmartValidator validator;

    private final RestaurantOutputRepresentationModelAssembler assembler;
    private final RestaurantInputRepresentationDisassembler disassembler;
    private final PaymentMethodOutputRepresentationModelAssembler paymentMethodAssembler;

    public RestaurantResources(final RestaurantRepository restaurantRepository,
                               final RestaurantRegistrationService restaurantRegistrationService,
                               final ObjectMapper objectMapper,
                               final SmartValidator validator,
                               final RestaurantOutputRepresentationModelAssembler assembler,
                               final RestaurantInputRepresentationDisassembler disassembler,
                               final PaymentMethodOutputRepresentationModelAssembler paymentMethodAssembler) {
        this.restaurantRepository = restaurantRepository;
        this.restaurantRegistrationService = restaurantRegistrationService;
        this.objectMapper = objectMapper;
        this.validator = validator;
        this.assembler = assembler;
        this.disassembler = disassembler;
        this.paymentMethodAssembler = paymentMethodAssembler;
    }

    @GetMapping
    public ResponseEntity<CollectionModel<RestaurantOutputRepresentation>> list() {
        final CollectionModel<RestaurantOutputRepresentation> representations = assembler.toCollectionModel(restaurantRepository.findAll());

        /*
        final List<RestaurantOutputRepresentation> list = restaurantRepository.findAll()
                .stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());
         */

        return ResponseEntity.ok()
                //.header(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "*")
                .body(representations);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{restaurantId}")
    public RestaurantOutputRepresentation getById(@PathVariable("restaurantId") Long id) {
        return restaurantRepository
                .findById(id)
                .map(assembler::toModel)
                .orElseThrow(() -> RestaurantNotFoundException.of(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RestaurantOutputRepresentation add(@RequestBody @Valid RestaurantInputRepresentation payload) {

        final Restaurant restaurant1 = disassembler.toDomainObject(payload);

        final Restaurant added = restaurantRegistrationService.add(restaurant1);

        ResourceUriHelper.addUriInResponseHeader(added.getId());

        return assembler.toModel(added);
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

        return assembler.toModel(updated);
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
    public ResponseEntity<Void> ativar(@PathVariable Long restaurantId) {
        restaurantRegistrationService.activate(restaurantId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{restaurantId}/activation")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> inactivate(@PathVariable Long restaurantId) {
        restaurantRegistrationService.inactivate(restaurantId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{restaurantId}/payment-methods")
    public CollectionModel<PaymentMethodOutputRepresentation> getRestaurantPaymentMethods(@PathVariable Long restaurantId) {
        return paymentMethodAssembler.toCollectionModel(restaurantRepository.getRestaurantPaymentMethods(restaurantId));
    }

    @PutMapping("/{restaurantId}/payment-methods/{paymentMethodId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void addPaymentMethod(@PathVariable(name = "restaurantId") Long restaurantId,
                                 @PathVariable(name = "paymentMethodId") Long paymentMethodId) {
        restaurantRegistrationService.addPaymentMethod(restaurantId, paymentMethodId);

    }

    @DeleteMapping("/{restaurantId}/payment-methods/{paymentMethodId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removePaymentMethod(@PathVariable(name = "restaurantId") Long restaurantId,
                                    @PathVariable(name = "paymentMethodId") Long paymentMethodId) {
        restaurantRegistrationService.removePaymentMethod(restaurantId, paymentMethodId);
    }

    /**
     * <code>
     * curl --location --request PUT 'http://localhost:8080/restaurants/activations' \
     * --header 'Content-Type: application/json' \
     * --data-raw '[ 1, 2 ]'
     * </code>
     */
    @PutMapping("/activations")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activeMultiples(@RequestBody List<Long> restaurantIds) {
        restaurantRegistrationService.activate(restaurantIds);
    }
}
