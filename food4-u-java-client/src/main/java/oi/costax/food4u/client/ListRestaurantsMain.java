package oi.costax.food4u.client;

import oi.costax.food4u.client.api.RestaurantClient;
import org.springframework.web.client.RestTemplate;

public class ListRestaurantsMain {

    public static void main(String[] args) {

            RestTemplate restTemplate = new RestTemplate();

            RestaurantClient restauranteClient = new RestaurantClient(
                    restTemplate, "http://localhost:8080");

            restauranteClient.list()
                    .forEach(System.out::println);
    }
}
