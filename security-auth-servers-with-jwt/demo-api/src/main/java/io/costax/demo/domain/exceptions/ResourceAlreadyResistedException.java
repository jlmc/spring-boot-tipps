package io.costax.demo.domain.exceptions;

public class ResourceAlreadyResistedException extends IllegalArgumentException {


    public ResourceAlreadyResistedException(final String s) {
        super(s);
    }

    public ResourceAlreadyResistedException(final String s, Object... obj) {
        super(String.format(s, obj));
    }
}
