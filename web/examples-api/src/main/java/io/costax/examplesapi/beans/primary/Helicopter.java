package io.costax.examplesapi.beans.primary;

import org.springframework.stereotype.Component;

@Component
public class Helicopter implements Flying {

    @Override
    public String fly(final Object transportable) {
        return String.format("Transporting [%s] using [%s]", transportable, getClass().getSimpleName());
    }
}
