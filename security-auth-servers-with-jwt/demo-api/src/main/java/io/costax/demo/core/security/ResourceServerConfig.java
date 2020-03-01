package io.costax.demo.core.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Esta classe tem de extender a classe WebSecurityConfigurerAdapter, porque o objectivo é fazer override do metodo:
 * <p>
 * protected void configure(final HttpSecurity http) throws Exception {
 */
@Configuration
@EnableWebSecurity // enable the spring web security
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class ResourceServerConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(final HttpSecurity http) throws Exception {

        http
                .csrf().disable()
                .cors().and()
                .oauth2ResourceServer().jwt()
                .jwtAuthenticationConverter(jwtAuthenticationConverter());

        //@formatter:off
        /*
        http
            .authorizeRequests()
                // define all free request '**' represent any thing
                //.antMatchers("/ping").permitAll()
                    .antMatchers(HttpMethod.GET, "/ping").hasAuthority("MANAGER_USERS")
                    .antMatchers(HttpMethod.POST, "/books/**").hasAuthority("EDIT_BOOKS")
                    .antMatchers(HttpMethod.GET, "/books/**").hasAuthority("SEE_BOOKS")
                    .antMatchers(HttpMethod.POST, "/users/**").hasAuthority("MANAGER_USERS")
                    // authorize any authenticated request
                    .anyRequest().authenticated()
                .and()
                .csrf().disable()
                .cors()
                .and()
                .oauth2ResourceServer()
                    .jwt()
                    .jwtAuthenticationConverter(jwtAuthenticationConverter())
        ;

         */
        //@formatter:on
    }

    /**
     * Enable the load of the JWT "authorities",
     * only using this we can have access to that information and can use it in the system.
     * <p>
     * The default beaver it use the "scopes" as Authorities.
     */
    private JwtAuthenticationConverter jwtAuthenticationConverter() {
        var jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwt -> {

            List<String> authorities = jwt.getClaimAsStringList("authorities");

            // Convert the Claim authorities in to GrantedAuthority
            final Stream<SimpleGrantedAuthority> grantedAuthorities = Optional.ofNullable(authorities)
                    .stream()
                    .flatMap(Collection::stream)
                    .map(SimpleGrantedAuthority::new);
                    //.collect(Collectors.toUnmodifiableSet());

            // Converter the Claim scopes in to GrantedAuthority, this is the default converter
            final JwtGrantedAuthoritiesConverter scopesGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
            final Stream<GrantedAuthority> grantedScopes = scopesGrantedAuthoritiesConverter.convert(jwt).stream();

            return Stream.concat(grantedScopes, grantedAuthorities).collect(Collectors.toUnmodifiableSet());
        });

        return jwtAuthenticationConverter;
    }

    /*
    @Bean
    public JwtDecoder jwtDecoder() {
         // Note: Only important if we use JWT symmetric key
        // symmetric algorithm, note that the key must be a 32 bytes at least: HmacSHA256
        final SecretKey secureKey = new SecretKeySpec("12345689_12345689_12345689_12_abcdefghijklmnopqrstuvxz_12345689_12345689_12345689_12".getBytes(), "HmacSHA256");
        return NimbusJwtDecoder.withSecretKey(secureKey).build();
    }
    */

}
