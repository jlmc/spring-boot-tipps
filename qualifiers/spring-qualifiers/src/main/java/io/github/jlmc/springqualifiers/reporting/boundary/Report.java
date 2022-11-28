package io.github.jlmc.springqualifiers.reporting.boundary;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Report {

    String operation() default "";
}
