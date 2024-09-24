package io.github.jlmc.poc.configurations.openfeign;

import feign.RetryableException;
import feign.Retryer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FixedDelayRetryer implements Retryer {

    private static final Logger LOGGER = LoggerFactory.getLogger(FixedDelayRetryer.class);

    private final int maxAttempts;
    private final long delayInMilliseconds;
    private int attempt = 1;

    public FixedDelayRetryer() {
        this(5, 5_000);  // Default: 5 attempts with 1-second backoff
    }

    public FixedDelayRetryer(int maxAttempts, long delayInMilliseconds) {
        this.maxAttempts = maxAttempts;
        this.delayInMilliseconds = delayInMilliseconds;
    }

    @Override
    public void continueOrPropagate(RetryableException e) {
        if (attempt++ >= maxAttempts) {
            LOGGER.debug("Propagate the exception if the maximum {} attempts have been reached! ", maxAttempts);
            throw e; // Propagate the exception if the maximum attempts have been reached
        }

        try {
            LOGGER.debug("Retrying after {} attempts", attempt);
            Thread.sleep(delayInMilliseconds); // Wait for the specified backoff before retrying
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public Retryer clone() {
        return new FixedDelayRetryer(maxAttempts, delayInMilliseconds);
    }
}
