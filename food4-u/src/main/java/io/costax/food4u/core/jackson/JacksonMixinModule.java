package io.costax.food4u.core.jackson;

import com.fasterxml.jackson.databind.module.SimpleModule;
import io.costax.food4u.api.model.mixin.RestaurantMixin;
import io.costax.food4u.domain.model.Restaurant;
import org.springframework.stereotype.Component;

@Component
public class JacksonMixinModule extends SimpleModule {

    public JacksonMixinModule() {
        setMixInAnnotation(Restaurant.class, RestaurantMixin.class);
    }
}
