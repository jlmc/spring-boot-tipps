package io.costax.demo.core.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
public class SecurityHelper {

    private SecurityContext getContext() {
        return SecurityContextHolder
                .getContext();
    }

    /**
     * The Authentication represent the authenticated Token
     */
    public Authentication getAuthentication() {
        return getContext()
                .getAuthentication();
    }

    public Long getUserId() {
        final Authentication authentication = getAuthentication();

        final Jwt jwt = (Jwt) authentication.getPrincipal();
        final Long userId = jwt.getClaim("user_id");

        return userId;
    }

    public Collection<? extends GrantedAuthority> getAuthorities() {
        return getAuthentication().getAuthorities();
    }
}
