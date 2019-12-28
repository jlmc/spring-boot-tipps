package io.costax.food4u.domain.model;

import io.costax.food4u.domain.exceptions.WrongCurrentPasswordException;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Data
@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String name;

    @Email
    @Column(nullable = false)
    private String email;

    @NotBlank
    @Column(nullable = false)
    private String pw;

    @CreationTimestamp
    @Column(nullable = false, columnDefinition = "datetime(6)")
    private OffsetDateTime registrationAt;

    @ManyToMany
    @JoinTable(name = "user_grup",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "group_id"))
    private Set<Group> grupos = new HashSet<>();

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        final User user = (User) o;
        return getId() != null && Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    public void setEmail(final String email) {
        this.email = email !=  null ? email.trim().toLowerCase() : null;
    }

    public void changePassword(final String currentPassword, final String newPassword) {
       if (!Objects.equals(this.pw, currentPassword)) {
           throw WrongCurrentPasswordException.of(this.getEmail());
       }

       this.pw = newPassword;
    }
}
