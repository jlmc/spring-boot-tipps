package io.costax.food4u.api.assembler.paymentmethods.output;

import io.costax.food4u.api.PaymentMethodResources;
import io.costax.food4u.api.model.paymentmethods.output.PaymentMethodOutputRepresentation;
import io.costax.food4u.domain.model.PaymentMethod;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Component
public class PaymentMethodOutputRepresentationModelAssembler
        extends RepresentationModelAssemblerSupport<PaymentMethod, PaymentMethodOutputRepresentation> {

    @Autowired
    ModelMapper modelMapper;

    public PaymentMethodOutputRepresentationModelAssembler() {
        super(PaymentMethodResources.class, PaymentMethodOutputRepresentation.class);
    }

    @Override
    public PaymentMethodOutputRepresentation toModel(final PaymentMethod paymentMethod) {
        final PaymentMethodOutputRepresentation model = createModelWithId(paymentMethod.getId(), paymentMethod);

        modelMapper.map(paymentMethod, model);

        model.add(linkTo(PaymentMethodResources.class).withRel(IanaLinkRelations.COLLECTION));

        return model;
    }
}
