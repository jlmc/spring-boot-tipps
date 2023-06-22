package io.github.jlmc.uploadcsv.locations.entity;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Transient;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static io.github.jlmc.uploadcsv.locations.entity.Location.LOCATIONS;

@Document(collection = LOCATIONS)


@Data
@Builder(toBuilder = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class Location {

    public static final String IMAGE_URL_REGEX = "^(http|https)://[a-zA-Z0-9-.]+.[a-zA-Z]{2,}(/[^/]*)*/[^/]+.(jpg|jpeg|png|webp)$";

    public static final String LOCATIONS = "locations";

    @EqualsAndHashCode.Include
    @ToString.Include

    @Id
    private String id;
    @ToString.Include
    @Indexed(name = "accountId_1", background = true)
    private String accountId;
    @ToString.Include
    private String name;
    private String imageUrl;
    private String phoneNumber;
    private ZoneId timeZone;
    private Address address;
    private Map<DayOfWeek, Set<Slot>> openHours;

    @CreatedDate
    private Instant createdDate;

    @LastModifiedDate
    private Instant lastModifiedDate;

    @Version
    private Integer version;

    @Transient
    public Location update(Location that) {
        this.name = that.name;
        this.imageUrl = that.imageUrl;
        this.phoneNumber = that.phoneNumber;
        this.timeZone = that.timeZone;
        this.address = that.address;
        this.openHours = that.openHours;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Location location)) return false;

        return Objects.equals(id, location.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
