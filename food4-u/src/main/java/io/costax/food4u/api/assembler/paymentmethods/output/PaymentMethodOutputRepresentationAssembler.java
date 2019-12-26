package io.costax.food4u.api.assembler.paymentmethods.output;

import io.costax.food4u.api.assembler.Assembler;
import io.costax.food4u.api.model.paymentmethods.output.PaymentMethodOutputRepresentation;
import io.costax.food4u.domain.model.PaymentMethod;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PaymentMethodOutputRepresentationAssembler implements Assembler<PaymentMethodOutputRepresentation, PaymentMethod> {

    @Autowired
    ModelMapper modelMapper;

    @Override
    public PaymentMethodOutputRepresentation toRepresentation(final PaymentMethod paymentMethod) {
        if (paymentMethod == null) return null;
        return modelMapper.map(paymentMethod, PaymentMethodOutputRepresentation.class);
    }
}
