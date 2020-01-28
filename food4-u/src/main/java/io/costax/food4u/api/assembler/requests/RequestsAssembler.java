package io.costax.food4u.api.assembler.requests;

import io.costax.food4u.api.assembler.Disassembler;
import io.costax.food4u.api.model.requests.input.RequestInputRepresentation;
import io.costax.food4u.domain.model.Request;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RequestsAssembler implements
        Disassembler<Request, RequestInputRepresentation> {
        //Assembler<RequestOutputRepresentation, Request> {

    @Autowired
    ModelMapper modelMapper;

    @Override
    public Request toDomainObject(final RequestInputRepresentation payload) {
        return modelMapper.map(payload, Request.class);
    }

}
