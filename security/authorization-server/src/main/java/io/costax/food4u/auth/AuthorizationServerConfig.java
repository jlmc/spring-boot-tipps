package io.costax.food4u.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;

/**
 * Enable the project to be an Authorization-Server application
 */
@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserDetailsService userDetailsService;

    /**
     * Configure the clients apps details
     */
    @Override
    public void configure(final ClientDetailsServiceConfigurer clients) throws Exception {
        //super.configure(clients);

        //@formatter:off
        clients
              // the first version is in memory
              .inMemory()
                // client app identifier (client-id)
                .withClient("food4u-web")
                // app-client-secret (client-key)
                .secret(passwordEncoder.encode("web123"))
                // specify what flows we want to use for the current client app
                .authorizedGrantTypes("password", "refresh_token")
                // scopes for the current client app
                .scopes("write", "read")
                .accessTokenValiditySeconds(60 * 60 * 6) // 6 hours (default is 12 hours)
                .refreshTokenValiditySeconds(12 * 60 * 60) // 12 hours
              .and()
                .withClient("food4u-api")
                // app-client-secret (client-key)
                .secret(passwordEncoder.encode("food4u-api-123"))
              .and()
                .withClient("food4u-batch-app")
                .secret(passwordEncoder.encode("food4u-batch-app123"))
                .authorizedGrantTypes("client_credentials")
                .accessTokenValiditySeconds(6 * 24 * 60 * 60) // 6 days (default is 12 hours)
                .scopes("read")
        // Define other clients

        //@formatter:on

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
     * Configure who have access to the OAuth 2.0 Token Introspection endpoint
     */
    @Override
    public void configure(final AuthorizationServerSecurityConfigurer security) throws Exception {
        //super.configure(security);

        // configure who have access to the OAuth 2.0 Token Introspection endpoint
        // the parameter of that we put in the checkTokenAccess is a Spring security expression,
        // and in the present example we are defining that all the authenticated clients have access. so the client-app have to perform the request with app-client-id and app-client-secret
        // other possible options to the spring security expression could be for example: `permitAll()` in that case we client don't have to provide any thing to validate the tokens
        security.checkTokenAccess("isAuthenticated()");
    }

    /**
     * Only the Resource Owner Password Credentials Grant flow need this configurations
     */
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) {
        endpoints
                // Only the Resource Owner Password Credentials Grant flow need this configurations of the authenticationManager
                .authenticationManager(authenticationManager)
                .userDetailsService(userDetailsService)
                .reuseRefreshTokens(false)
        ;
    }
}
