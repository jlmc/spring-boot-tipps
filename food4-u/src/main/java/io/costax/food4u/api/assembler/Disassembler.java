package io.costax.food4u.api.assembler;

public interface Disassembler<DOMAIN, INPUT> {

    DOMAIN toDomainObject(INPUT payload);
}
