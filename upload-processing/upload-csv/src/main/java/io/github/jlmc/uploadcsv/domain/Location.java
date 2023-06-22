package io.github.jlmc.uploadcsv.domain;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import org.springframework.data.annotation.*;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.IntStream;

import static io.github.jlmc.uploadcsv.domain.Location.LOCATIONS;

@Document(collection = LOCATIONS)


@Data
@Builder(toBuilder = true)
@ToString(onlyExplicitlyIncluded = true)
public class Location {

    public static final String IMAGE_URL_REGEX = "^(http|https)://[a-zA-Z0-9-.]+.[a-zA-Z]{2,}(/[^/]*)*/[^/]+.(jpg|jpeg|png|webp)$";

    public static final String LOCATIONS = "locations";
    public static final String INACTIVE_LOCATIONS = "inactive_locations";

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
    @Builder.Default
    private Set<SpecialDay> specialDays = new HashSet<>();

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

    public List<DailyAvailabilities> dailyAvailabilitiesOf(Instant since, int plusDays) {
        ZonedDateTime zonedDateTime = since.atZone(this.timeZone);

        return IntStream.range(0, plusDays).mapToObj(zonedDateTime::plusDays)
                        .map(this::dailyAvailabilities)
                        .toList();
    }

    private DailyAvailabilities dailyAvailabilities(ZonedDateTime zonedDateTime) {
        var specialDay =
                specialDays.stream()
                           .sorted(Comparator.comparing(it -> it.repeats().ordinal()))
                           .filter(it -> it.match(zonedDateTime.toLocalDate()))
                           .findFirst();

        if (specialDay.isPresent()) {
            SpecialDay specialDay1 = specialDay.get();
            String name1 = specialDay1.name();
            List<Slot> list =
                    specialDay1.slots()
                               .stream()
                               .map(Slot::copy)
                               .sorted(Slot.COMPARATOR)
                               .toList();
            return new DailyAvailabilities(zonedDateTime.toLocalDate(), list, name1);
        } else {
            List<Slot> list =
                    openHours.getOrDefault(zonedDateTime.getDayOfWeek(), Collections.emptySet())
                             .stream()
                             .map(Slot::copy)
                             .sorted(Slot.COMPARATOR)
                             .toList();

            return new DailyAvailabilities(zonedDateTime.toLocalDate(), list);
        }
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
