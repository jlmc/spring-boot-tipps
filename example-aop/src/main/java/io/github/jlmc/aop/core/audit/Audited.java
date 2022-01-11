package io.github.jlmc.aop.core.audit;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Audited {

    String operation() default  "";

    String idParameterName() default "";

    Severity severity() default Severity.MINOR;

    enum Severity {
        CRITICAL(3),
        MAJOR(2),
        MINOR(1);

        private final int value;

        Severity(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

}
