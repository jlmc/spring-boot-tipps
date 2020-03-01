package io.xine.authserverswithjwt.core;

import io.xine.authserverswithjwt.domain.model.User;
import io.xine.authserverswithjwt.domain.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.stream.Collectors;

@Qualifier("X")
@Service
public class JpaUserDetailsService implements UserDetailsService {

    @Autowired
    UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
        return userRepository.findByEmailIgnoreCase(username)
                .map(u -> AuthUser.of(u, getAuthorities(u)))
                .orElseThrow(() -> new UsernameNotFoundException(String.format("User with the username '%s'", username)));
    }

    private Collection<? extends GrantedAuthority> getAuthorities(final User user) {
        return user.getGroups()
                .stream()
                .flatMap(g -> g.getPermissions().stream())
                .distinct()
                .map(permission -> new SimpleGrantedAuthority(permission.getName().toUpperCase()))
                .collect(Collectors.toUnmodifiableSet());
    }
}
