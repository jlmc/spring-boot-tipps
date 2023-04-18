package io.github.jlmc.uploadcsv.locations.boundary.csv.locations.model;

import io.github.jlmc.uploadcsv.locations.entity.Address;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AddressCsvResourceTest {

    private final AddressCsvResource prototype =
            AddressCsvResource.builder().address("Av 1")
                              .city("Alverca")
                              .regionName("Lisbon")
                              .zipCode("123").coordinates(new CoordinatesCsvResource(10.0, 11.1))
                              .build();

    @Test
    void onEquals() {
        prototype.toBuilder().build();

        assertEquals(prototype.toBuilder().build(), prototype.toBuilder().build());
        assertNotSame(prototype.toBuilder().build(), prototype.toBuilder().build());
        assertNotEquals(prototype.toBuilder().build(), prototype.toBuilder().address("Av2").build());
    }

    @Test
    void toEntity() {
        AddressCsvResource input = prototype.toBuilder().build();
        Address entity = input.toEntity();

        assertNotNull(entity);
    }
}
