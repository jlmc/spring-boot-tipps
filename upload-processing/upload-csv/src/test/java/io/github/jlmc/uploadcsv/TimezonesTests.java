package io.github.jlmc.uploadcsv;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Set;

public class TimezonesTests {

    @Test
    void allTimeZones() {
        Set<String> availableZoneIds = ZoneId.getAvailableZoneIds();

        availableZoneIds.forEach(System.out::println);
    }

    @Test
    void table() {
        // Test
        Instant instant = Instant.now();
        System.out.println("Timezone offset at " + instant);
        System.out.println("==============================================");
        ZoneId.getAvailableZoneIds()
                .stream()
                .sorted()
                .forEach(strZoneId -> System.out.printf("%-35s: %-6s \t %s %n",
                        strZoneId, getTzOffsetString(ZoneId.of(strZoneId), instant), instant.atZone(ZoneId.of(strZoneId))));
    }

    static String getTzOffsetString(ZoneId zoneId, Instant instant) {
        return ZonedDateTime.ofInstant(instant, zoneId).getOffset().toString();
    }

}
