package io.xine.authserverswithjwt.core;

import io.xine.authserverswithjwt.domain.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class JpaUserDetailsService implements UserDetailsService {

    @Autowired
    UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
        return userRepository.findByEmailIgnoreCase(username)
                .map(AuthUser::of)
                .orElseThrow(() -> new UsernameNotFoundException(String.format("User with the username '%s'", username)));
    }
}
