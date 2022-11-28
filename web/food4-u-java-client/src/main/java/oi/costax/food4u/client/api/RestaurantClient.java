package oi.costax.food4u.client.api;

import oi.costax.food4u.client.model.RestaurantModel;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Arrays;
import java.util.List;

public class RestaurantClient {

    private static final String RESOURCE_PATH = "/restaurants";

    private RestTemplate restTemplate;
    private String url;

    public RestaurantClient(final RestTemplate restTemplate, final String url) {
        this.restTemplate = restTemplate;
        this.url = url;
    }

    public List<RestaurantModel> list() {

        try {
            URI resourceUri = URI.create(url + RESOURCE_PATH);

            RestaurantModel[] restaurants = restTemplate
                    .getForObject(resourceUri, RestaurantModel[].class);

            return Arrays.asList(restaurants);

        } catch (RestClientResponseException e) {
            throw new ApiClientException(e.getMessage(), e);
        }
    }
}
