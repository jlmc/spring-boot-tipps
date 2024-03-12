package io.github.jlmc.cwr.service.domain.players.entities;

import io.github.jlmc.cwr.service.domain.common.AuditingData;
import jakarta.persistence.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "players")
@EntityListeners(AuditingEntityListener.class)
public class Player implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "players_seq")
    @SequenceGenerator(
            name = "players_seq",
            allocationSize = 5
    )
    private Long id;
    @Column(name = "name", nullable = false)
    private String name;
    @Column(name = "birthdate")
    private LocalDate birthdate;
    @Column(name = "country_code", nullable = false)
    private String countryCode;
    @Version
    @Column(nullable = false)
    private short version;
    @Embedded
    private AuditingData auditingData = new AuditingData();

    public Player() {
    }

    private Player(String name, LocalDate birthdate, String countryCode) {
        this.name = name;
        this.birthdate = birthdate;
        this.countryCode = countryCode;
        this.auditingData = new AuditingData();
    }

    public static Player newPlayerWith(String name, LocalDate birthdate, String countryCode) {
        Objects.requireNonNull(name);
        Objects.requireNonNull(birthdate);
        Objects.requireNonNull(countryCode);
        return new Player(name, birthdate, countryCode);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public LocalDate getBirthdate() {
        return birthdate;
    }

    public String getCountryCode() {
        return countryCode;
    }
}
