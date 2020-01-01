package io.costax.food4u.api.assembler.requests;

import io.costax.food4u.api.assembler.Assembler;
import io.costax.food4u.api.model.requests.output.RequestSummaryOutputRepresentation;
import io.costax.food4u.domain.model.Request;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RequestSummaryAssembler
        implements Assembler<RequestSummaryOutputRepresentation, Request> {

    @Autowired
    ModelMapper modelMapper;

    @Override
    public RequestSummaryOutputRepresentation toRepresentation(final Request request) {
        return modelMapper.map(request, RequestSummaryOutputRepresentation.class);
    }
}
