package io.github.jlmc.cwr.service.domain.clubs.commands;

import java.util.Set;

public record DefineClubSeasonCommand(Long clubId, String season, Set<Long> playerIds) {
}
