package io.costax.food4u.api.model.restaurants.input;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddressInputRepresentation {
    private String street;
    private String city;
    private String zipCode;
}
