package io.costax.food4u.domain.services;

import io.costax.food4u.domain.model.PaymentMethod;
import io.costax.food4u.domain.repository.PaymentMethodRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PaymentMethodRegistrationService {

    private final PaymentMethodRepository repository;

    public PaymentMethodRegistrationService(final PaymentMethodRepository repository) {
        this.repository = repository;
    }

    public PaymentMethod add(PaymentMethod paymentMethod) {
        return repository.save(paymentMethod);
    }

}
