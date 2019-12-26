package io.costax.food4u.core.jackson;

import com.fasterxml.jackson.databind.module.SimpleModule;
import io.costax.food4u.api.model.mixin.CookerMixin;
import io.costax.food4u.api.model.mixin.RestaurantMixin;
import io.costax.food4u.domain.model.Cooker;
import io.costax.food4u.domain.model.Restaurant;
import org.springframework.stereotype.Component;

@Component
public class JacksonMixinModule extends SimpleModule {

    public JacksonMixinModule() {
        setMixInAnnotation(Cooker.class, CookerMixin.class);
        setMixInAnnotation(Restaurant.class, RestaurantMixin.class);
    }
}
