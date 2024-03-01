package io.github.jlmc.cwr.service.domain.clubs.entities;

import io.github.jlmc.cwr.service.domain.clubs.repository.SeasonConverter;
import io.github.jlmc.cwr.service.domain.players.entities.Player;
import jakarta.persistence.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "teams")
public class Team implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "teams_seq")
    @SequenceGenerator(
            name = "teams_seq",
            allocationSize = 5
    )
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "club_id", nullable = false, updatable = false)
    private Club club;

    @Convert(converter = SeasonConverter.class)
    @Column(name = "season", nullable = false, updatable = false)
    private Season season;

    @ManyToMany(
            fetch = FetchType.LAZY,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE}
    )
    @JoinTable(name = "teams_players",
            joinColumns = @JoinColumn(name = "team_id"),
            inverseJoinColumns = @JoinColumn(name = "player_id"))
    private Set<Player> players = new HashSet<>();

    public Set<Player> getPlayers() {
        return Collections.unmodifiableSet(this.players);
    }

    Team() {}

    private Team(Season season) {
        this.season = season;
    }

    public static Team createTeam(Season season) {
        Objects.requireNonNull(season);
        return new Team(season);
    }

    public Team addPlayer(Player issue) {
        this.players.add(issue);
        return this;
    }

    public Team removePlayer(Player player) {
        this.players.remove(player);
        return this;
    }

    public Long getId() {
        return id;
    }

    public Club getClub() {
        return club;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Team team = (Team) o;
        return id != null && Objects.equals(id, team.id);
    }

    @Override
    public int hashCode() {
        return id == null ? 31 : Objects.hash(id);
    }

    public void setClub(Club club) {
        this.club = club;
    }

    public Season getSeason() {
        return season;
    }
}
