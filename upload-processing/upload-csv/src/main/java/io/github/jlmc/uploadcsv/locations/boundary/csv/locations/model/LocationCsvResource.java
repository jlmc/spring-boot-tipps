package io.github.jlmc.uploadcsv.locations.boundary.csv.locations.model;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvCustomBindByName;
import com.opencsv.bean.CsvRecurse;
import io.github.jlmc.uploadcsv.locations.boundary.csv.locations.converters.CsvZoneIdConverter;
import io.github.jlmc.uploadcsv.locations.boundary.csv.locations.converters.SlotCsvResourceListConverter;
import io.github.jlmc.uploadcsv.locations.entity.Location;
import io.github.jlmc.uploadcsv.locations.entity.Slot;
import lombok.Data;
import org.springframework.data.util.Pair;

import java.time.DayOfWeek;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.github.jlmc.uploadcsv.locations.boundary.csv.locations.model.Columns.*;

@Data
public class LocationCsvResource {
    @CsvBindByName(column = LOCATION_ID)
    String id;
    @CsvBindByName(required = true, column = LOCATION_NAME)
    String name;
    @CsvRecurse
    AddressCsvResource address;
    @CsvBindByName(column = IMAGE_URL)
    String imageUrl;
    @CsvBindByName(column = CONTACT_PHONE)
    String phoneNumber;
    @CsvCustomBindByName(column = TIMEZONE, converter = CsvZoneIdConverter.class, required = true)
    ZoneId timeZone;
    @CsvCustomBindByName(column = BUSINESS_MON, converter = SlotCsvResourceListConverter.class)
    List<SlotCsvResource> businessSlotsMonday;
    @CsvCustomBindByName(column = BUSINESS_TUE, converter = SlotCsvResourceListConverter.class)
    List<SlotCsvResource> businessSlotsTuesday;
    @CsvCustomBindByName(column = BUSINESS_WED, converter = SlotCsvResourceListConverter.class)
    List<SlotCsvResource> businessSlotsWednesday;
    @CsvCustomBindByName(column = BUSINESS_THU, converter = SlotCsvResourceListConverter.class)
    List<SlotCsvResource> businessSlotsThursday;
    @CsvCustomBindByName(column = BUSINESS_FRI, converter = SlotCsvResourceListConverter.class)
    List<SlotCsvResource> businessSlotsFriday;
    @CsvCustomBindByName(column = BUSINESS_SAT, converter = SlotCsvResourceListConverter.class)
    List<SlotCsvResource> businessSlotsSaturday;
    @CsvCustomBindByName(column = BUSINESS_SUN, converter = SlotCsvResourceListConverter.class)
    List<SlotCsvResource> businessSlotsSunday;

    public static LocationCsvResource from(Location entity) {
        LocationCsvResource resource = new LocationCsvResource();
        resource.id = entity.getId();
        resource.name = entity.getName();
        resource.address = Optional.ofNullable(entity.getAddress()).map(entityAddress -> {
            return new AddressCsvResource(
                    entityAddress.getAddress(),
                    entityAddress.getZipCode(),
                    entityAddress.getCity(),
                    entityAddress.getRegionName(), entityAddress.getCountryName(),
                    Optional.ofNullable(entityAddress.getCoordinates()).map(entityAddressCoordenates -> new CoordinatesCsvResource(entityAddressCoordenates.getLatitude(), entityAddressCoordenates.getLongitude())).orElse(null)
                  );
        }).orElse(null);
        resource.imageUrl = entity.getImageUrl();
        resource.phoneNumber = entity.getPhoneNumber();
        resource.timeZone = entity.getTimeZone();

        resource.businessSlotsMonday = toBusinessSlots(entity, DayOfWeek.MONDAY);
        resource.businessSlotsTuesday = toBusinessSlots(entity, DayOfWeek.TUESDAY);
        resource.businessSlotsWednesday = toBusinessSlots(entity, DayOfWeek.WEDNESDAY);
        resource.businessSlotsThursday = toBusinessSlots(entity, DayOfWeek.THURSDAY);
        resource.businessSlotsFriday = toBusinessSlots(entity, DayOfWeek.FRIDAY);
        resource.businessSlotsSaturday = toBusinessSlots(entity, DayOfWeek.SATURDAY);
        resource.businessSlotsSunday = toBusinessSlots(entity, DayOfWeek.SUNDAY);

        return resource;
    }

    private static List<SlotCsvResource> toBusinessSlots(Location entity, DayOfWeek dayOfWeek) {
        Map<DayOfWeek, Set<Slot>> openHours = entity.getOpenHours();

        if (openHours == null) {
            return Collections.emptyList();
        }

        return businessSlots(openHours.getOrDefault(dayOfWeek, Collections.emptySet()));
    }

    private static List<SlotCsvResource> businessSlots(Set<Slot> entitySlots) {
        return entitySlots.stream().map(SlotCsvResource::from).toList();
    }

    public Map<DayOfWeek, Set<Slot>> getOpenHours() {
        return Stream.of(

                             Pair.of(DayOfWeek.MONDAY, Optional.ofNullable(businessSlotsMonday)),
                             Pair.of(DayOfWeek.TUESDAY, Optional.ofNullable(businessSlotsTuesday)),
                             Pair.of(DayOfWeek.WEDNESDAY, Optional.ofNullable(businessSlotsWednesday)),
                             Pair.of(DayOfWeek.THURSDAY, Optional.ofNullable(businessSlotsThursday)),
                             Pair.of(DayOfWeek.FRIDAY, Optional.ofNullable(businessSlotsFriday)),
                             Pair.of(DayOfWeek.SATURDAY, Optional.ofNullable(businessSlotsSaturday)),
                             Pair.of(DayOfWeek.SUNDAY, Optional.ofNullable(businessSlotsSunday))
                     )
                     .filter(pair -> pair.getSecond().isPresent())
                     .map(entry -> {
                         DayOfWeek key = entry.getFirst();

                         var slots =
                                 entry.getSecond()
                                      .map(i -> i.stream()
                                                 .map(SlotCsvResource::toEntity)
                                                 .collect(Collectors.toSet()))
                                      .orElse(Set.of());

                         return Map.entry(key, slots);
                     }).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }


}
