package io.xine.authserverswithjwt.core;

import io.xine.authserverswithjwt.domain.model.User;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class AuthUser extends org.springframework.security.core.userdetails.User {

    private String fullName;
    private Integer userId;

    private AuthUser(final String username,
                     final String password,
                     final Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
    }

    public static AuthUser of(User user, final Collection<? extends GrantedAuthority> authorities) {
        final AuthUser authUser = new AuthUser(user.getEmail(), user.getPw(), authorities);
        authUser.setFullName(user.getName());
        authUser.setUserId(user.getId());
        return authUser;
    }

    private void setFullName(final String fullName) {
        this.fullName = fullName;
    }

    private void setUserId(final Integer userId) {
        this.userId = userId;
    }

    public String getFullName() {
        return fullName;
    }

    public Integer getUserId() {
        return userId;
    }
}
