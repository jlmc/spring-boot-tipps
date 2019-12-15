package io.costax.food4u.api;

import io.costax.food4u.domain.model.Restaurant;
import io.costax.food4u.domain.repository.RestaurantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("tests")
public class TestsResource {

    @Autowired
    RestaurantRepository restaurantRepository;

    @GetMapping("/restaurants/by-name")
    public List<Restaurant> searchByName(@RequestParam("name") String name) {
        return restaurantRepository.findByName(name);
    }

    @GetMapping("/restaurants/by-takeAwayTax")
    public List<Restaurant> searchBetween(@RequestParam(value = "init", defaultValue = "0.0") BigDecimal init,
                                         @RequestParam(value = "until", defaultValue = "10.0") BigDecimal until) {
        return restaurantRepository.findByTakeAwayTaxBetween(init, until);
    }

    @GetMapping("/restaurants/first-takeAwayTax")
    public Restaurant firstBetween(@RequestParam(value = "init", defaultValue = "0.0") BigDecimal init,
                                         @RequestParam(value = "until", defaultValue = "10.0") BigDecimal until) {
        return restaurantRepository.findFirstByTakeAwayTaxBetween(init, until);
    }
}
