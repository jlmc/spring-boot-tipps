package io.github.jlmc.cwr.service.domain.clubs.entities;

import io.github.jlmc.cwr.service.domain.common.AuditingData;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "clubs")
@EntityListeners(AuditingEntityListener.class)
public class Club implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "clubs_seq")
    @SequenceGenerator(
            name = "clubs_seq",
            allocationSize = 5
    )
    private Long id;
    @NotNull
    @Column(name = "name")
    private String name;
    @Version
    @Column(nullable = false)
    private short version;
    @Embedded
    private AuditingData auditingData = new AuditingData();
    @OneToMany(
            mappedBy = "club",
            orphanRemoval = true, // when the orphanRemoval is set with true the CascadeType.REMOVE is redundant
            //cascade = CascadeType.ALL
            cascade = {
                    CascadeType.PERSIST,
                    //CascadeType.REMOVE,
                    //CascadeType.DETACH,
                    CascadeType.MERGE
            })
    private Set<Team> teams = new HashSet<>();

    Club() {}

    private Club(String name) {
        this.name = name;
    }

    public static Club createClub(String name) {
        Objects.requireNonNull(name);
        return new Club(name);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public short getVersion() {
        return version;
    }

    public AuditingData getAuditingData() {
        return auditingData;
    }

    public Set<Team> getTeams() {
        return teams;
    }

    public Club addTeam(Team team) {
        team.setClub(this);
        teams.add(team);
        return this;
    }

    public Club removeTeam(Team team) {
        team.setClub(null);
        teams.remove(team);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Club club = (Club) o;
        return id != null && Objects.equals(id, club.id);
    }

    @Override
    public int hashCode() {
        return id == null ? 31 : Objects.hash(id);
    }
}
