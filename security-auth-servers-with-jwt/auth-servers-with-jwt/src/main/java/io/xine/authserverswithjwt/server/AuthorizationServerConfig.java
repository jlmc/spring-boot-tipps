package io.xine.authserverswithjwt.server;


import io.xine.authserverswithjwt.core.JwtCustomClaimsTokenEnhancer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.approval.ApprovalStore;
import org.springframework.security.oauth2.provider.approval.TokenApprovalStore;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;

import java.security.KeyPair;
import java.util.Arrays;

/**
 * Enable the project to be an Authorization-Server application
 */
@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {

    @Autowired
    JwtKeyStoreProperties jwtKeyStoreProperties;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    @Qualifier("X")
    UserDetailsService userDetailsService;

    @Bean
    public JwtAccessTokenConverter jwtAccessTokenConverter() {
        JwtAccessTokenConverter converter = new JwtAccessTokenConverter();

        // We have two possibilities,
        // 1. we can use a symmetric key, the authentication-server and the resource-server use the same key
        // 2. we can use a unsymmetrical key, the authentication-server use a private key to assign the JWT
        // and the resource-server use a public key to verify the integrity of the JWT


        // If we use the symmetric signature key, we can use the method converter.setSigningKey(String); to define the key
        //converter.setSigningKey("12345689_12345689_12345689_12_abcdefghijklmnopqrstuvxz_12345689_12345689_12345689_12");


        // If we use unsymmetrical key
        final ClassPathResource jksResource = new ClassPathResource(jwtKeyStoreProperties.getPath());
        final String keyStorePass = jwtKeyStoreProperties.getPassword();
        final String keyPairAlias = jwtKeyStoreProperties.getKeypairAlias(); // // inside jks can exists multiple Keypairs, we need to select only one Pair

        // open the file
        final KeyStoreKeyFactory keyStoreKeyFactory = new KeyStoreKeyFactory(jksResource, keyStorePass.toCharArray());
        // get the key pair with the alias
        final KeyPair keyPair = keyStoreKeyFactory.getKeyPair(keyPairAlias);

        converter.setKeyPair(keyPair);
        return converter;
    }

    /**
     * Configure the clients apps details
     */
    @Override
    public void configure(final ClientDetailsServiceConfigurer clients) throws Exception {
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
                    .scopes("WRITE", "READ")
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
                    .scopes("READ")
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
                    .scopes("WRITE", "READ")
                    // define the accepted redirect uri for authorization-code generation
                    .redirectUris("http://food4u.local:8000")
                .and()
                    .withClient("webadmin")
                    // Authentication Implicit Grant Flow
                    .authorizedGrantTypes("implicit")
                    .scopes("WRITE", "READ")
                    .redirectUris("http://food4u.local:8000") // devia usar outra
        ;
        //@formatter:on
    }

    /**
     * Only the Resource Owner Password Credentials Grant flow need this configurations
     */
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) {

        final TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
        tokenEnhancerChain.setTokenEnhancers(
                Arrays.asList(new JwtCustomClaimsTokenEnhancer(), jwtAccessTokenConverter()));


        //@formatter:off
        endpoints
                // Only the Resource Owner Password Credentials Grant flow need this configurations of the authenticationManager
                .authenticationManager(authenticationManager)
                .userDetailsService(userDetailsService)
                .reuseRefreshTokens(false)
                // add the converter
                .accessTokenConverter(jwtAccessTokenConverter())
                // add custom jwt token chain
                .tokenEnhancer(tokenEnhancerChain)
                // must be called after the method accessTokenConverter
                .approvalStore(approvalStore(endpoints.getTokenStore()))
        ;
        //@formatter:on
    }

    /**
     * Permite a aprovação granular dos scopes, Necessário quando se usa o JWT
     */
    private ApprovalStore approvalStore(final TokenStore tokenStore) {
        final TokenApprovalStore tokenApprovalStore = new TokenApprovalStore();
        tokenApprovalStore.setTokenStore(tokenStore);

        return tokenApprovalStore;
    }

    /**
     * Configure who have access to the OAuth 2.0 Token Introspection endpoint
     */
    @Override
    public void configure(final AuthorizationServerSecurityConfigurer security) {
        //super.configure(security);

        // configure who have access to the OAuth 2.0 Token Introspection endpoint
        // the parameter of that we put in the checkTokenAccess is a Spring security expression,
        // and in the present example we are defining that all the authenticated clients have access. so the client-app have to perform the request with app-client-id and app-client-secret
        // other possible options to the spring security expression could be for example: `permitAll()` in that case we client don't have to provide any thing to validate the tokens
        //security.checkTokenAccess("isAuthenticated()");
        security.checkTokenAccess("permitAll()")
                .tokenKeyAccess("permitAll()")
        //.allowFormAuthenticationForClients()
        ;
    }
}


