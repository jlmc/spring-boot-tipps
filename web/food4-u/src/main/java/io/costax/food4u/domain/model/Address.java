package io.costax.food4u.domain.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class Address implements Serializable {

    private static final long serialVersionUID = 1L;

    private String street;
    private String city;
    @Column(name = "zip_code")
    private String zipCode;


    @Deprecated
    protected Address() {
    }

    private Address(final String street, final String city, final String zipCode) {
        this.street = street;
        this.city = city;
        this.zipCode = zipCode;
    }

    public static Address of(final String street, final String city, final String zipCode) {
        return new Address(street, city, zipCode);
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(final String street) {
        this.street = street;
    }

    public String getCity() {
        return city;
    }

    public void setCity(final String city) {
        this.city = city;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(final String zipCode) {
        this.zipCode = zipCode;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof Address)) return false;
        final Address address = (Address) o;
        return Objects.equals(street, address.street) &&
                Objects.equals(city, address.city) &&
                Objects.equals(zipCode, address.zipCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(street, city, zipCode);
    }

    @Override
    public String toString() {
        return "Address{" +
                "street='" + street + '\'' +
                ", city='" + city + '\'' +
                ", zipCode='" + zipCode + '\'' +
                '}';
    }
}
