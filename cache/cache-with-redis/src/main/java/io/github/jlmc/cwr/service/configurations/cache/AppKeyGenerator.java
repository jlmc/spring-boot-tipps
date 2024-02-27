package io.github.jlmc.cwr.service.configurations.cache;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.interceptor.KeyGenerator;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * A key é muito importante para legibilidade. Desta forma acordamos (sujeito a discussão) em seguir esta convenção:
 * <service_name>:<cache_name>::<nome_metodo>:<identificador>
 */
public class AppKeyGenerator implements KeyGenerator {

    private final String serviceName;

    public AppKeyGenerator(String serviceName) {
        this.serviceName = serviceName;
    }

    @Override
    public Object generate(Object target, Method method, Object... params) {
        String methodName = method.getName();
        String cacheName = getCacheName(method);

        if (params.length == 0) {
            return "%s:%s::%s"
                    .formatted(
                            serviceName,
                            cacheName,
                            methodName);
        }

        String paramsHashCodeJoined =
                Arrays.stream(params)
                .map(Object::hashCode)
                .map(Object::toString)
                .collect(Collectors.joining(","));

        return "%s:%s::%s:%s".formatted(
                serviceName,
                cacheName,
                methodName,
                paramsHashCodeJoined
        );
    }

    private String getCacheName(Method method) {
        Cacheable cacheable = method.getAnnotation(Cacheable.class);

        if (cacheable != null && cacheable.cacheNames().length > 0) {
            return cacheable.cacheNames()[0];
        }

        Class<?> declaringClass = method.getDeclaringClass();
        CacheConfig cacheConfig = declaringClass.getAnnotation(CacheConfig.class);

        if (cacheConfig != null && cacheConfig.cacheNames().length > 0) {
            return cacheConfig.cacheNames()[0];
        }

        throw new IllegalStateException("No Cacheable cache name defined.");
    }
}
