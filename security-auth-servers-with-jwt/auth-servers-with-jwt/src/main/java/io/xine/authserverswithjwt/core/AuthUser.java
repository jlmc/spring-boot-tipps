package io.xine.authserverswithjwt.core;

import io.xine.authserverswithjwt.domain.User;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Collections;

public class AuthUser extends org.springframework.security.core.userdetails.User {

    private String fullName;

    private AuthUser(final String username,
                     final String password,
                     final Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
    }

    public static AuthUser of(User user) {
        final AuthUser authUser = new AuthUser(user.getEmail(), user.getPw(), Collections.emptyList());
        return authUser;
    }

    private void setFullName(final String fullName) {
        this.fullName = fullName;
    }
}
