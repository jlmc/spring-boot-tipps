package io.github.jlmc.cwr.service.domain.players.repository;

import io.github.jlmc.cwr.service.domain.players.entities.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlayersRepository extends JpaRepository<Player, Long> {
}
