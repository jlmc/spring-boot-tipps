package io.github.jlmc.uploadcsv.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.util.Comparator;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
public class Slot {
    public static final Comparator<Slot> COMPARATOR = Comparator.comparing(Slot::getOpenAt).thenComparing(Slot::getCloseAt);

    private LocalTime openAt;
    private LocalTime closeAt;

    public Slot copy() {
        return this.toBuilder().build();
    }
}
