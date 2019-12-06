package io.costax.examplesapi.qualifiercustomannotation;

import org.springframework.beans.factory.annotation.Qualifier;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Qualifier
@Retention(RetentionPolicy.RUNTIME)
public @interface InvoiceType {

    Type value() default Type.HIGH;
}
