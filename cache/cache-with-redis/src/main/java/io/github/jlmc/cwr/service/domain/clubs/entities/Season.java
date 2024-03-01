package io.github.jlmc.cwr.service.domain.clubs.entities;

import java.io.Serial;
import java.io.Serializable;

public record Season(String title) implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
}
