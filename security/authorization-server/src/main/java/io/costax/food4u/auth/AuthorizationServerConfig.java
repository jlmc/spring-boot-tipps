package io.costax.food4u.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;

/**
 * Enable the project to be an Authorization-Server application
 */
@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    /**
     * Configure the clients apps details
     */
    @Override
    public void configure(final ClientDetailsServiceConfigurer clients) throws Exception {
        //super.configure(clients);
        clients
                // the first version is in memory
                .inMemory()
                // client app identifier (client-id)
                .withClient("food4u-web")
                // app-client-secret (client-key)
                .secret(passwordEncoder.encode("web123"))
                // specify what flows we want to use for the current client app
                .authorizedGrantTypes("password")
                // scopes for the current client app
                .scopes("write", "read")
                .accessTokenValiditySeconds(60 * 60 * 6) // 6 hours (default is 12 hours)
        // Define other clients
                /*

                .and()
                .withClient("food4u-mobile")
                // app-client-secret
                .secret(passwordEncoder.encode("web123"))
                // specify what flows we want to use for the current client app
                .authorizedGrantTypes("password")
                // scopes for the current client app
                .scopes("write", "read")

                 */
        ;


    }

    /**
     * Only the Resource Owner Password Credentials Grant flow need this configurations
     */
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) {
        // Only the Resource Owner Password Credentials Grant flow need this configurations
        endpoints.authenticationManager(authenticationManager);
    }
}
