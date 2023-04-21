package io.github.jlmc.tc.domain.todos.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.Instant;
import java.util.Objects;


@Getter
@Setter
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString


@Entity
@Table(name = "todos")


public class Todo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 100)

    @Column(name = "title", updatable = true, nullable = false, length = 100)
    private String title;

    @NotBlank
    @Size(max = 1_500)
    @Column(name = "description", updatable = true, nullable = false, length = 1_500)
    private String description;

    @JsonIgnore
    @CreationTimestamp
    private Instant created;

    @JsonIgnore
    @UpdateTimestamp
    private Instant updated;

    @JsonIgnore
    @Version
    private Long version;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Todo todo)) return false;
        return id != null && Objects.equals(id, todo.id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    public Todo updateWith(Todo input) {
        this.title = input.title;
        this.description = input.description;
        return this;
    }
}
