package io.costax.food4u.api;


import io.costax.food4u.domain.ResourceNotFoundException;
import io.costax.food4u.domain.model.Restaurant;
import io.costax.food4u.domain.repository.RestaurantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(
        value = "/restaurants"
        /*,
        produces = {
                MediaType.APPLICATION_JSON_VALUE
        },
        consumes = {
                MediaType.APPLICATION_JSON_VALUE
        }*/)
public class RestaurantResources {


    @Autowired
    private RestaurantRepository restaurantRepository;

    @GetMapping
    public List<Restaurant> list() {
        return restaurantRepository.findAll();
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{restaurantId}")
    public Restaurant getById(@PathVariable("restaurantId") Long id) {
        return restaurantRepository
                .findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of(Restaurant.class, id));
    }
}
