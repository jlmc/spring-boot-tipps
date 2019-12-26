package io.costax.food4u.api.assembler.paymentmethods.output;

import io.costax.food4u.api.assembler.Assembler;
import io.costax.food4u.api.model.paymentmethods.input.PaymentMethodOutputRepresentation;
import io.costax.food4u.domain.model.PaymentMethod;
import org.springframework.stereotype.Component;

@Component
public class PaymentMethodOutputRepresentationAssembler implements Assembler<PaymentMethodOutputRepresentation, PaymentMethod> {

    @Override
    public PaymentMethodOutputRepresentation toRepresentation(final PaymentMethod paymentMethod) {
        if (paymentMethod == null) return null;

        final PaymentMethodOutputRepresentation paymentMethodOutputRepresentation = new PaymentMethodOutputRepresentation();
        paymentMethodOutputRepresentation.setId(paymentMethod.getId());
        paymentMethodOutputRepresentation.setName(paymentMethod.getName());
        return paymentMethodOutputRepresentation;
    }
}
