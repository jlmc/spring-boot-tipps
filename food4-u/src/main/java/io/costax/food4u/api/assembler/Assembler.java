package io.costax.food4u.api.assembler;

public interface Assembler<OUT, DOMAIN> {

    OUT toRepresentation(DOMAIN domain);
}
