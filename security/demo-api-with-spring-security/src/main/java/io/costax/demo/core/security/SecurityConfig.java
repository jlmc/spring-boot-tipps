package io.costax.demo.core.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Esta classe tem de extender a classe WebSecurityConfigurerAdapter, porque o objectivo é fazer override do metodo:
 * <p>
 * protected void configure(final HttpSecurity http) throws Exception {
 */
@Configuration
@EnableWebSecurity // enable the spring web security
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(final HttpSecurity http) throws Exception {
        //super.configure(http);

        //@formatter:off
        http
            .httpBasic()
            .and()
            .authorizeRequests()
                // define all free request '**' represent any thing
                .antMatchers("/ping").permitAll()
                // authorize any authenticated request
                .anyRequest().authenticated()
            // disable session manager and cookies, because a rest api should be stateless
            .and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            // disable csrf
            .and().csrf().disable()
        ;
        //@formatter:on
    }

    /**
     * Configure the autentication users methods
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
}
