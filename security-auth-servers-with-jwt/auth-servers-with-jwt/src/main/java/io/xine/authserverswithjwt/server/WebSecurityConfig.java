package io.xine.authserverswithjwt.server;

import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Configure Users Authentication Strategies
 */
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    /**
     * Configure the authentication users methods
     */
    @Override
    protected void configure(final AuthenticationManagerBuilder auth) throws Exception {
        //super.configure(auth);

        final PasswordEncoder passwordEncoder = passwordEncoder();
        auth.inMemoryAuthentication()
                .withUser("john")
                .password(passwordEncoder.encode("pwd"))
                .roles("ADMIN", "CLIENT")
                .and()
                .withUser("foo")
                .password(passwordEncoder.encode("pwd"))
                .roles("CLIENT")
        ;
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Only the Resource Owner Password Credentials Grant flow need this configurations
     */
    @Bean
    @Override
    protected AuthenticationManager authenticationManager() throws Exception {
        return super.authenticationManager();
    }

    @Bean
    @Override
    protected UserDetailsService userDetailsService() {
        return super.userDetailsService();
    }
}
