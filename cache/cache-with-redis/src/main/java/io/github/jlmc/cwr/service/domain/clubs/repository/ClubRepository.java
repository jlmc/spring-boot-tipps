package io.github.jlmc.cwr.service.domain.clubs.repository;

import io.github.jlmc.cwr.service.domain.clubs.entities.Club;
import io.github.jlmc.cwr.service.domain.clubs.entities.Season;
import io.github.jlmc.cwr.service.domain.clubs.entities.Team;
import jakarta.persistence.QueryHint;
import org.hibernate.jpa.HibernateHints;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.stereotype.Repository;

@Repository
public interface ClubRepository extends JpaRepository<Club, Long> {

    //@Cacheable(cacheNames = "club-season-team")
    @Query("select t from Team t left join fetch t.players p where t.club.id = :clubId and t.season = :season")
    //@Query("select t from Team t where t.club.id = :clubId and t.season = :season")

    //@QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value ="true") })
    @QueryHints({ @QueryHint(name = HibernateHints.HINT_CACHEABLE, value ="true") })
    Team findSeasonTeam(Long clubId, Season season);
}
