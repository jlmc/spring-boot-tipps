package io.costax.examplesapi.beans.primary;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Component
@Primary
public class Airplane implements Flying {

    @Override
    public String fly(final Object transportable) {
        return String.format("Transporting [%s] using [%s]", transportable, "Airplane");
    }
}
