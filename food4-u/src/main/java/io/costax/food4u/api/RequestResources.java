package io.costax.food4u.api;

import io.costax.food4u.api.assembler.requests.RequestsAssembler;
import io.costax.food4u.api.model.requests.input.RequestInputRepresentation;
import io.costax.food4u.api.model.requests.output.RequestOutputRepresentation;
import io.costax.food4u.domain.exceptions.ResourceNotFoundException;
import io.costax.food4u.domain.model.Request;
import io.costax.food4u.domain.model.User;
import io.costax.food4u.domain.repository.RequestRepository;
import io.costax.food4u.domain.services.RequestCreatorService;
import io.costax.food4u.domain.services.RequestFlowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/requests")
public class RequestResources {

    @Autowired
    RequestsAssembler requestsAssembler;

    @Autowired
    RequestCreatorService requestCreatorService;

    @Autowired
    RequestFlowService requestFlowService;

    @Autowired
    RequestRepository requestRepository;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RequestOutputRepresentation add(@Valid @RequestBody RequestInputRepresentation payload) {
        final Request request = requestsAssembler.toDomainObject(payload);

        // FIXME: 29/12/2019 - the current user should came from the login user
        User currentUser = User.createUser(1L);

        final Request record = requestCreatorService.create(currentUser, request);
        return requestsAssembler.toRepresentation(record);
    }

    @GetMapping("/{code}")
    public RequestOutputRepresentation getByCode(@PathVariable String code) {
        return requestRepository.getByCodeWithAllInformation(code)
                .map(requestsAssembler::toRepresentation)
                .orElseThrow(() -> new ResourceNotFoundException(Request.class, code));
    }

    @PutMapping("/{code}/confirmation")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void confirm(@PathVariable String code) {
        requestFlowService.confirm(code);
    }

    @PutMapping("/{code}/cancellation")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancel(@PathVariable String code) {
        requestFlowService.cancel(code);
    }

    @PutMapping("/{code}/delivery")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delivery(@PathVariable String code) {
        requestFlowService.delivery(code);
    }

}
