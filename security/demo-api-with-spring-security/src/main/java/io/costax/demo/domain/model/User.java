package io.costax.demo.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.util.HashSet;
import java.util.Set;

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

    public User() {
    }

    public Integer getId() {
        return this.id;
    }

    public String getUsername() {
        return this.username;
    }

    public @Email @NotBlank String getEmail() {
        return this.email;
    }

    public String getPwd() {
        return this.pwd;
    }

    public Set<Permission> getPermissions() {
        return this.permissions;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(@Email @NotBlank String email) {
        this.email = email;
    }

    @JsonIgnore
    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public void setPermissions(Set<Permission> permissions) {
        this.permissions = permissions;
    }

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof User)) return false;
        final User other = (User) o;
        if (!other.canEqual((Object) this)) return false;
        final Object this$id = this.getId();
        final Object other$id = other.getId();
        if (this$id == null ? other$id != null : !this$id.equals(other$id)) return false;
        final Object this$username = this.getUsername();
        final Object other$username = other.getUsername();
        if (this$username == null ? other$username != null : !this$username.equals(other$username)) return false;
        final Object this$email = this.getEmail();
        final Object other$email = other.getEmail();
        if (this$email == null ? other$email != null : !this$email.equals(other$email)) return false;
        final Object this$pwd = this.getPwd();
        final Object other$pwd = other.getPwd();
        if (this$pwd == null ? other$pwd != null : !this$pwd.equals(other$pwd)) return false;
        final Object this$permissions = this.getPermissions();
        final Object other$permissions = other.getPermissions();
        if (this$permissions == null ? other$permissions != null : !this$permissions.equals(other$permissions))
            return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof User;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $id = this.getId();
        result = result * PRIME + ($id == null ? 43 : $id.hashCode());
        final Object $username = this.getUsername();
        result = result * PRIME + ($username == null ? 43 : $username.hashCode());
        final Object $email = this.getEmail();
        result = result * PRIME + ($email == null ? 43 : $email.hashCode());
        final Object $pwd = this.getPwd();
        result = result * PRIME + ($pwd == null ? 43 : $pwd.hashCode());
        final Object $permissions = this.getPermissions();
        result = result * PRIME + ($permissions == null ? 43 : $permissions.hashCode());
        return result;
    }

    public String toString() {
        return "User(id=" + this.getId() + ", username=" + this.getUsername() + ", email=" + this.getEmail() + ", pwd=" + this.getPwd() + ", permissions=" + this.getPermissions() + ")";
    }
}
