package io.costax.demo.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Table(name = "t_user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String username;

    @Email
    @NotBlank
    private String email;

    @JsonIgnore
    private String pwd = "pwd";

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
            name = "t_user_permission",
            joinColumns = @JoinColumn(name = "user_id", nullable = false, updatable = false)
    )
    @Enumerated(EnumType.STRING)
    private Set<io.costax.demo.domain.model.Permission> permissions = new HashSet<>();
}
