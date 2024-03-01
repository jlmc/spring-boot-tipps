package io.github.jlmc.cwr.service.domain.clubs.repository;

import io.github.jlmc.cwr.service.domain.clubs.entities.Club;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClubRepository extends JpaRepository<Club, Long> {
}
