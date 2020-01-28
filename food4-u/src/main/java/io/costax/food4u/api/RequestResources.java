package io.costax.food4u.api;

import io.costax.food4u.api.assembler.requests.RequestSummaryAssembler;
import io.costax.food4u.api.assembler.requests.RequestsAssembler;
import io.costax.food4u.api.assembler.requests.RequestsRepresentationModelAssembler;
import io.costax.food4u.api.model.requests.input.RequestInputRepresentation;
import io.costax.food4u.api.model.requests.output.RequestOutputRepresentation;
import io.costax.food4u.api.model.requests.output.RequestSummaryOutputRepresentation;
import io.costax.food4u.api.openapi.controllers.RequestResourcesOpenApi;
import io.costax.food4u.core.data.PageableTranslator;
import io.costax.food4u.domain.exceptions.ResourceNotFoundException;
import io.costax.food4u.domain.filters.RequestFilter;
import io.costax.food4u.domain.model.Request;
import io.costax.food4u.domain.model.User;
import io.costax.food4u.domain.repository.RequestRepository;
import io.costax.food4u.domain.repository.RequestSpecifications;
import io.costax.food4u.domain.services.RequestCreatorService;
import io.costax.food4u.domain.services.RequestFlowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path = "/requests", produces = MediaType.APPLICATION_JSON_VALUE)
public class RequestResources implements RequestResourcesOpenApi {

    @Autowired
    RequestsAssembler requestsAssembler;

    @Autowired
    RequestsRepresentationModelAssembler assembler;

    @Autowired
    RequestCreatorService requestCreatorService;

    @Autowired
    RequestFlowService requestFlowService;

    @Autowired
    RequestRepository requestRepository;

    @Autowired
    RequestSummaryAssembler requestSummaryAssembler;

    /**
     * http://localhost:8080/requests?clientId=1&restaurantActive=true
     * for pagination
     * http://localhost:8080/requests?size=1&page=1&sort=code,desc
     */
    @GetMapping
    public Page<RequestSummaryOutputRepresentation> search(final RequestFilter filters,
                                                           @PageableDefault(size = 10, page = 0) Pageable pageable) {

        pageable = translatePageableProperties(pageable);

        final Page<Request> pageOfDomain = requestRepository
                .findAll(RequestSpecifications.withFilters(filters), pageable);


        final List<RequestSummaryOutputRepresentation> requestSummaryOutputRepresentations = requestSummaryAssembler.toListOfRepresentations(pageOfDomain.getContent());

        return new PageImpl<>(requestSummaryOutputRepresentations, pageable, pageOfDomain.getTotalElements());
    }

    private Pageable translatePageableProperties(final Pageable pageable) {
        final Map<String, String> dictionary = Map.of(
                "code", "code",
                "subTotal", "subTotal",
                "takeAwayTax", "takeAwayTax",
                "totalValue", "totalValue",
                "status", "status",
                "createdAt", "createdAt",
                "restaurantName", "restaurant.name",
                "clientName", "client.name"
        );

        return PageableTranslator.translate(pageable, dictionary);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RequestOutputRepresentation add(@Valid @RequestBody RequestInputRepresentation payload) {
        final Request request = requestsAssembler.toDomainObject(payload);

        // FIXME: 29/12/2019 - the current user should came from the login user
        User currentUser = User.createUser(1L);

        final Request record = requestCreatorService.create(currentUser, request);

        ResourceUriHelper.addUriInResponseHeader(record);

        return assembler.toModel(record);
    }

    @GetMapping("/{code}")
    public RequestOutputRepresentation getByCode(@PathVariable String code) {
        return requestRepository.getByCodeWithAllInformation(code)
                .map(assembler::toModel)
                .orElseThrow(() -> new ResourceNotFoundException(Request.class, code));
    }

    @PutMapping("/{code}/confirmation")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> confirm(@PathVariable String code) {
        requestFlowService.confirm(code);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PutMapping("/{code}/cancellation")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> cancel(@PathVariable String code) {
        requestFlowService.cancel(code);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PutMapping("/{code}/delivery")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> delivery(@PathVariable String code) {
        requestFlowService.delivery(code);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
