package io.github.jlmc.uploadcsv.heroes.entity;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.Instant;

public record HeroResponseRepresentation(
        String id,
        String name,
        String nickName,
       // @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd'T'HH:mm:ss.SSSZ",timezone = "UTC")
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        Instant createAt) {
}
