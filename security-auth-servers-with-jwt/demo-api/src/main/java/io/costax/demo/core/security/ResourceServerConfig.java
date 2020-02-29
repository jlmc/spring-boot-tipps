package io.costax.demo.core.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * Esta classe tem de extender a classe WebSecurityConfigurerAdapter, porque o objectivo é fazer override do metodo:
 * <p>
 * protected void configure(final HttpSecurity http) throws Exception {
 */
@Configuration
@EnableWebSecurity // enable the spring web security
public class ResourceServerConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(final HttpSecurity http) throws Exception {
        //super.configure(http);

        //@formatter:off
        http
            .authorizeRequests()
                // define all free request '**' represent any thing
                .antMatchers("/ping").permitAll()
                // authorize any authenticated request
                .anyRequest().authenticated()
                .and()
                .cors()
                .and()
                .oauth2ResourceServer()
                .jwt()
        ;
        //@formatter:on
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
