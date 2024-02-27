package io.github.jlmc.cwr.service.configurations.cache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheManager;

@Configuration
public class AppCachingConfigurer implements CachingConfigurer {

    @Value("${spring.application.name}")
    String applicationName;

    //  @Bean(name = "transfersCacheManager")
    @Autowired
    @Qualifier("transfersCacheManager")
    private RedisCacheManager transfersCacheManager;


    @Autowired
    @Qualifier("appCacheManager")
    private RedisCacheManager appCacheManager;

    @Override
    @Bean
    public CacheManager cacheManager() {
        return appCacheManager;
    }

    /**
     * A key é muito importante para legibilidade. Desta forma acordamos (sujeito a discussão) em seguir esta convenção:
     * <service_name>:<cache_name>::<nome_metodo>:<identificador>
     */
    @Bean // important!
    @Override
    public KeyGenerator keyGenerator() {
        return new AppKeyGenerator(applicationName);
    }
}
