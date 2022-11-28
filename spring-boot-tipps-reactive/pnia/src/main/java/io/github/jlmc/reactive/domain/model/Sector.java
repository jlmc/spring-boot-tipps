package io.github.jlmc.reactive.domain.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Objects;

@Data
public class Sector {
    private String number;
    @JsonProperty("sector")
    private String name;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Sector sector)) return false;
        return Objects.equals(this.name, sector.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return "Sector{" +
                "number='" + number + '\'' +
                ", sector='" + name + '\'' +
                '}';
    }
}
