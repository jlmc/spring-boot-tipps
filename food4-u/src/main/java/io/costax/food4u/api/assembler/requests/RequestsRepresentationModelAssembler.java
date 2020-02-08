package io.costax.food4u.api.assembler.requests;

import io.costax.food4u.api.ApiLinks;
import io.costax.food4u.api.RequestResources;
import io.costax.food4u.api.model.requests.output.RequestOutputRepresentation;
import io.costax.food4u.domain.model.Request;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

@Component
public class RequestsRepresentationModelAssembler extends RepresentationModelAssemblerSupport<Request, RequestOutputRepresentation> {

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    ApiLinks apiLinks;

    public RequestsRepresentationModelAssembler() {
        super(RequestResources.class, RequestOutputRepresentation.class);
    }

    @Override
    public RequestOutputRepresentation toModel(final Request entity) {
        final RequestOutputRepresentation model = createModelWithId(entity.getCode(), entity);

        modelMapper.map(entity, model);

        // add all the others links
        model.add(
                //apiLinks.requestsLink(IanaLinkRelations.COLLECTION.value()),
                apiLinks.requestsLinks(IanaLinkRelations.COLLECTION.value()),
                apiLinks.requestsConfirmationLink(entity.getCode(), "confirmation"),
                apiLinks.requestsCancelLink(entity.getCode(), "cancel"),
                apiLinks.requestsDeliveryLink(entity.getCode(), "delivery")
        );

        return model;
    }


}
