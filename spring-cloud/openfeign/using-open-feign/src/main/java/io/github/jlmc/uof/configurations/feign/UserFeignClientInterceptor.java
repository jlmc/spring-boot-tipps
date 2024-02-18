package io.github.jlmc.uof.configurations.feign;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import feign.Retryer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;

public class UserFeignClientInterceptor implements RequestInterceptor {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserFeignClientInterceptor.class);

    private static final String X_MARKET_BEARER = "X-Market-Bearer";

    private final MarketplaceConfigurationProperties marketplaceConfigurationProperties;

    public UserFeignClientInterceptor(MarketplaceConfigurationProperties marketplaceConfigurationProperties) {
        this.marketplaceConfigurationProperties = marketplaceConfigurationProperties;
    }

    @Bean
    public Retryer retryer() {
        return new Retryer.Default(100L, 1000L, 5);
    }

    @Override
    public void apply(RequestTemplate requestTemplate) {
        LOGGER.info("Append {} token  to the request.", X_MARKET_BEARER);

        String accessToken = marketplaceConfigurationProperties.authenticationToken();
        requestTemplate.header(HttpHeaders.AUTHORIZATION, String.format("%s %s", X_MARKET_BEARER, accessToken));
    }
}
