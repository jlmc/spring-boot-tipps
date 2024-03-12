package io.github.jlmc.cwr.service.configurations.cache;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.cache.CacheManagerCustomizer;
import org.springframework.boot.autoconfigure.cache.CacheManagerCustomizers;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.redis.cache.RedisCache;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair;

import java.time.Duration;

/**
 * @see org.springframework.boot.autoconfigure.cache.CacheAutoConfiguration
 * @see org.springframework.boot.autoconfigure.cache.CacheCondition
 * @see org.springframework.boot.autoconfigure.cache.RedisCacheConfiguration
 * @see org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer
 * @see <a href="https://medium.com/vedity/spring-boot-caching-mechanism-8ef901147e60">Spring Boot Caching Mechanism</a>
 * @see <a href="https://medium.com/@jerome.waibel/caching-with-spring-boot-and-redis-can-be-tricky-5f99548601b9">Caching with Spring Boot and Redis can be tricky!</a>
 */
@Configuration
@EnableCaching
@EnableAspectJAutoProxy
@EnableConfigurationProperties(CacheProperties.class)
//@Import(CacheAutoConfiguration.class)
public class AppCacheConfiguration {

    @Value("${spring.application.name}")
    String applicationName;

    private static final Logger LOGGER = LoggerFactory.getLogger(AppCacheConfiguration.class);

    @Bean(name = "transfersCacheManager")
    RedisCacheManager transfersCacheManager( RedisConnectionFactory redisConnectionFactory, ObjectMapper objectMapper) {
        RedisCacheManager.RedisCacheManagerBuilder builder = RedisCacheManager.builder(redisConnectionFactory);

        RedisCacheConfiguration redisCacheConfiguration =
                RedisCacheConfiguration.defaultCacheConfig()
                        .entryTtl(Duration.ofMinutes(2))
                        .serializeValuesWith(jsonSerializationPair(objectMapper))
                ;

        builder.withCacheConfiguration("transfers", redisCacheConfiguration);

        return builder.build();
    }

    @Bean(name = {"appCacheManager"})
    RedisCacheManager appCacheManager(CacheProperties cacheProperties,
                                      CacheManagerCustomizers cacheManagerCustomizers,
                                      ObjectProvider<RedisCacheConfiguration> redisCacheConfiguration,
                                      ObjectProvider<RedisCacheManagerBuilderCustomizer> redisCacheManagerBuilderCustomizers,
                                      RedisConnectionFactory redisConnectionFactory,
                                      ResourceLoader resourceLoader) {

        LOGGER.info("Create the application cache manager");

        RedisCacheManager redisCacheManager = DefaultCacheManagerFactory.INSTANCE.cacheManager(
                cacheProperties,
                cacheManagerCustomizers,
                redisCacheConfiguration,
                redisCacheManagerBuilderCustomizers,
                redisConnectionFactory,
                resourceLoader
        );
        return redisCacheManager;
    }


    @Bean
    @ConditionalOnMissingBean
    public CacheManagerCustomizers cacheManagerCustomizers(ObjectProvider<CacheManagerCustomizer<?>> customizers) {
        return new CacheManagerCustomizers(customizers.orderedStream().toList());
    }

    @Bean
    public CacheManagerCustomizer transfersCustomizer(ObjectMapper objectMapper) {
        return new CacheManagerCustomizer() {
            @Override
            public void customize(CacheManager cacheManager) {
                System.out.println("--->");

                Cache cache = cacheManager.getCache(applicationName);

                if (cache instanceof RedisCache redisCache) {

                }
                System.out.println(cache);

            }
        };
        /*return builder -> {

            var config =
                    RedisCacheConfiguration.defaultCacheConfig()
                            .disableCachingNullValues()
                            .entryTtl(Duration.ofMillis(1))
                            .serializeValuesWith(jsonSerializationPair(objectMapper));


            builder.withCacheConfiguration("transfersX", config);
        };*/
    }


    SerializationPair<Object> jsonSerializationPair(ObjectMapper objectMapper) {
        return SerializationPair.fromSerializer(
                new GenericJackson2JsonRedisSerializer(objectMapper)
        );
    }
}
