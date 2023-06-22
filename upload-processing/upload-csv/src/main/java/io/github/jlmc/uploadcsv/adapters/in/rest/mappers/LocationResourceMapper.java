package io.github.jlmc.uploadcsv.adapters.in.rest.mappers;

import io.github.jlmc.uploadcsv.adapters.in.rest.resources.DayOfWeekSlots;
import io.github.jlmc.uploadcsv.adapters.in.rest.resources.LocationResource;
import io.github.jlmc.uploadcsv.adapters.in.rest.resources.SlotResource;
import io.github.jlmc.uploadcsv.domain.Location;
import io.github.jlmc.uploadcsv.domain.Slot;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.factory.Mappers;

import java.time.DayOfWeek;
import java.util.Map;
import java.util.Set;

import static java.util.stream.Collectors.toSet;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface LocationResourceMapper {

    LocationResourceMapper MAPPER = Mappers.getMapper(LocationResourceMapper.class);

    @Mapping(
            target = "timeZone",
            expression = "java(java.util.Optional.ofNullable(location.getTimeZone()).map(java.time.ZoneId::getId).orElse(null))")
    @Mapping(target = "openHours", expression = "java(openHoursToOpenHoursResources(location.getOpenHours()))")
    LocationResource toResource(Location location);

    @SuppressWarnings("unused")
    default Set<DayOfWeekSlots> openHoursToOpenHoursResources(Map<DayOfWeek, Set<Slot>> openHours) {
        return openHours.entrySet().stream()
                .map(it -> {
                    var slots = it.getValue().stream()
                            .map(s -> new SlotResource(s.getOpenAt(), s.getCloseAt()))
                            .collect(toSet());
                    return new DayOfWeekSlots(it.getKey(), slots);
                }).collect(toSet());
    }
}
