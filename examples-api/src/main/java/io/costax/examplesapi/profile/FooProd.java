package io.costax.examplesapi.profile;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("prod")
public class FooProd implements Foo {

    @Override
    public String message() {
        return "foo from prod";
    }
}
