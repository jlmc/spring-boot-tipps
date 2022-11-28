package io.costax.food4u.api.model.restaurants.output;

import io.costax.food4u.domain.model.Address;
import io.swagger.annotations.ApiModel;
import lombok.Data;

@ApiModel(value = "AddressOutput")
@Data
public class AddressOutputRepresentation {

    private String street;
    private String city;
    private String zipCode;

    public static AddressOutputRepresentation of(Address address) {
        if (address == null) return null;
        AddressOutputRepresentation representation = new AddressOutputRepresentation();
        representation.street = address.getStreet();
        representation.city = address.getCity();
        representation.zipCode = address.getZipCode();
        return representation;
    }
}
