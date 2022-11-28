package io.costax.food4u.domain.services;

import io.costax.food4u.domain.exceptions.ResourceNotFoundException;
import io.costax.food4u.domain.model.Request;
import io.costax.food4u.domain.repository.RequestRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;

@Service
public class RequestFlowService {

    private final RequestRepository requestRepository;
    private final Clock clock;

    // FIXME: 30/12/2019 All the transitions can throw a IllegalStateException the api need to catch that exception

    public RequestFlowService(final RequestRepository requestRepository, final Clock clock) {
        this.requestRepository = requestRepository;
        this.clock = clock;
    }

    @Transactional
    public void confirm(final String code) {
        final Request request = requestRepository.findBySimpleNaturalId(code)
                .orElseThrow(() -> new ResourceNotFoundException(Request.class, code));

        request.confirm(clock);
    }

    @Transactional
    public void cancel(final String code) {
        final Request request = requestRepository.findBySimpleNaturalId(code)
                .orElseThrow(() -> new ResourceNotFoundException(Request.class, code));

        request.cancel(clock);
    }

    @Transactional
    public void delivery(final String code) {
        final Request request = requestRepository.findBySimpleNaturalId(code)
                .orElseThrow(() -> new ResourceNotFoundException(Request.class, code));

        request.delivery(clock);
    }
}
