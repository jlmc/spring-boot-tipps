package io.costax.food4u.domain.services;

import io.costax.food4u.domain.exceptions.CookerNotFoundException;
import io.costax.food4u.domain.exceptions.ResourceInUseException;
import io.costax.food4u.domain.model.Cooker;
import io.costax.food4u.domain.repository.CookerRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CookerRegistrationService {

    private final CookerRepository repository;

    public CookerRegistrationService(final CookerRepository repository) {
        this.repository = repository;
    }

    public Cooker add(final Cooker cooker) {
        return repository.save(cooker);
    }

    public void remove(final Long cookerId) {
        Cooker cooker = repository.findById(cookerId)
                .orElseThrow(() -> CookerNotFoundException.of(cookerId));

        try {
            repository.delete(cooker);
            repository.flush();
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            throw ResourceInUseException.of(Cooker.class, cookerId);
        }
    }

    public Cooker update(final Long cookerId, final Cooker cooker) {
        Cooker cookerCurrent = repository.findById(cookerId)
                .orElseThrow(() -> CookerNotFoundException.of(cookerId));

        BeanUtils.copyProperties(cooker, cookerCurrent, "id");
        repository.flush();

        return cookerCurrent;
    }
}
