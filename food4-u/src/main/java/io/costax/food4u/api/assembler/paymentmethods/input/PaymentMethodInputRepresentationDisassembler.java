package io.costax.food4u.api.assembler.paymentmethods.input;

import io.costax.food4u.api.assembler.Disassembler;
import io.costax.food4u.api.model.paymentmethods.input.PaymentMethodInputRepresentation;
import io.costax.food4u.domain.model.PaymentMethod;
import org.springframework.stereotype.Component;

@Component
public class PaymentMethodInputRepresentationDisassembler implements Disassembler<PaymentMethod, PaymentMethodInputRepresentation> {

    @Override
    public PaymentMethod toDomainObject(PaymentMethodInputRepresentation payload) {
        if (payload == null) return null;

        return PaymentMethod.of(payload.getName());
    }
}
