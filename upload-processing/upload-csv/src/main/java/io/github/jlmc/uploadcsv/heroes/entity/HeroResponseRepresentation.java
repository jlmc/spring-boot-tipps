package io.github.jlmc.uploadcsv.heroes.entity;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.Instant;

public record HeroResponseRepresentation(
        String id,
        String name,
        String nickName,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        Instant createAt) {
}
