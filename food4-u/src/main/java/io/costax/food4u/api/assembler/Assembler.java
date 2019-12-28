package io.costax.food4u.api.assembler;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public interface Assembler<OUT, DOMAIN> {

    OUT toRepresentation(DOMAIN domain);

    default List<OUT> toListOfRepresentations(Collection<DOMAIN> domains) {
        Objects.requireNonNull(domains);
        return domains.stream().map(this::toRepresentation).collect(Collectors.toList());
    }
}
