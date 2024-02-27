package io.github.jlmc.cwr.service.domain.configurations;

import jakarta.persistence.*;
import org.hibernate.annotations.Immutable;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.Objects;

@Entity
@Table(name = "configuration")
@Immutable

@EntityListeners(AuditingEntityListener.class)
//@Audited
public class Configuration {
    @Id
    @Column(name = "key", nullable = false, unique = true)
    private String name;
    private String value;

    @Version
    @Column(nullable = false)
    private Long version;

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreatedDate
    private Instant createdAt;

    @Column(name = "modified_at", nullable = false)
    @LastModifiedDate
    private Instant modifiedAt;

    @Column(name = "created_by", nullable = false, updatable = false)
    @CreatedBy
    private String createdBy;

    @Column(name = "modified_by", nullable = false)
    @LastModifiedBy
    private String modifiedBy;

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Configuration that = (Configuration) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return "Configuration{" +
                "name='" + name + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
