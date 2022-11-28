package io.github.jlmc.food4u.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.CompositeTokenGranter;
import org.springframework.security.oauth2.provider.TokenGranter;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;

import java.util.Arrays;

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

    @Autowired
    RedisConnectionFactory connectionFactory;

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
                // this client is for the access_token introspection
                .withClient("food4u-api")
                    // app-client-secret (client-key)
                    .secret(passwordEncoder.encode("food4u-api-123"))
            .and()
                // client for the client_credentials flow
                .withClient("food4u-batch-app")
                    .secret(passwordEncoder.encode("food4u-batch-app123"))
                    .authorizedGrantTypes("client_credentials")
                    .accessTokenValiditySeconds(6 * 24 * 60 * 60) // 6 days (default is 12 hours)
                    .scopes("read")
            .and()
                .withClient("food4uAnalytics")
                    // Client example with authorization_code flow
                    // 1. generation of authorization-code
                    //      http://AUTHORIZATION-SERVER:8081/oauth/authorize?
                    //      response_type=code
                    //      &client_id=food4u-analytics-client-id
                    //      &state=ABC
                    //      &redirect_uri=http//food4u.local
                    .secret(passwordEncoder.encode("food4uAnalytics"))
                    .authorizedGrantTypes("authorization_code")
                    .scopes("write", "read")
                    // define the accepted redirect uri for authorization-code generation
                    .redirectUris("http://food4u.local:8000")
            .and()
                .withClient("webadmin")
                    // Authentication Implicit Grant Flow
                    .authorizedGrantTypes("implicit")
                    .scopes("write", "read")
                    .redirectUris("http://food4u.local:8000") // devia usar outra
        ;
        //@formatter:on
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
                // append token Granter for PKCE in Authentication-code-flow
                .tokenGranter(tokenGranter(endpoints))
                // define a token store to be able to restart the Authorization-Server
                // without lose the tokens,
                // be sides its allows us to share the token between multiples instance of the Authorization-Server
                .tokenStore(tokenStore())
        ;
    }

    /**
     * Define the token store to store and share the token
     * between instance o the Authorization-Server.
     *
     * <p>
     *     We have some options to choice the TokenStore implementation:
     *      - InMemoryTokenStore (default)
     *      - JdbcTokenStore
     *      - JwkTokenStore
     *      - RedisTokenStore
     * </p>
     *
     * In this example we are configuring the redis implementation
     */
    private TokenStore tokenStore() {
        return new RedisTokenStore(connectionFactory);
    }

    /**
     * append support for PKCE code flow
     */
    private TokenGranter tokenGranter(AuthorizationServerEndpointsConfigurer endpoints) {
        var pkceAuthorizationCodeTokenGranter = new PkceAuthorizationCodeTokenGranter(endpoints.getTokenServices(),
                endpoints.getAuthorizationCodeServices(), endpoints.getClientDetailsService(),
                endpoints.getOAuth2RequestFactory());

        var granters = Arrays.asList(
                pkceAuthorizationCodeTokenGranter, endpoints.getTokenGranter());

        return new CompositeTokenGranter(granters);
    }
}
