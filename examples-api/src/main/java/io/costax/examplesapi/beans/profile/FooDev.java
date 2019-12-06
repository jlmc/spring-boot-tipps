package io.costax.examplesapi.beans.profile;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("dev")
public class FooDev implements Foo {

    @Override
    public String message() {
        return "foo from dev";
    }
}
