package io.github.jlmc.cwr.service.common;


import java.util.List;

public record SeasonTeam(String season, List<PlayerRepresentation> players) {
}
