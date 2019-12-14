package io.costax.food4u;

import io.costax.food4u.domain.model.Restaurant;
import io.costax.food4u.domain.repository.RestaurantRepository;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContext;

import java.util.List;
import java.util.stream.Collectors;

public class SeAppQueriesMain {

    public static void main(String[] args) {
        ApplicationContext applicationContext = new SpringApplicationBuilder(Food4UApplication.class)
                .web(WebApplicationType.NONE)
                .run(args);

        RestaurantRepository restaurantRepository = applicationContext.getBean(RestaurantRepository.class);

        final List<Restaurant> restaurants = restaurantRepository.findById(1L).stream().collect(Collectors.toList());

        for (Restaurant r : restaurants) {
            System.out.println(r.getName());
        }
    }
}
