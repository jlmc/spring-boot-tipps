package io.github.jlmc.reactive.domain.services.prefixes;

import org.apache.commons.collections4.list.TreeList;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
public class PrefixesService {

    private static final String CACHE_NAME = "PREFIXES_SERVICE_CACHE";

    private final PrefixesConfiguration prefixesConfiguration;
    private final CacheManager cacheManager;

    private int maxPrefixSize = 7;
    private TreeList<PrefixEntry> prefixes = new TreeList<>();


    public PrefixesService(PrefixesConfiguration prefixesConfiguration, CacheManager cacheManager) {
        this.prefixesConfiguration = prefixesConfiguration;
        this.cacheManager = cacheManager;
    }


    public Mono<String> findByPhoneNumber(String phoneNumber) {
        var prefixResult = Mono.fromCallable(() -> findPrefix(phoneNumber));

        return prefixResult;

        /*
        return Flux.fromIterable(prefixes)
                   .filter(it -> it.match(phoneNumber))
                   .take(1)
                   .log("Prefix found for " + phoneNumber)
                   .map(it -> it.prefix)
                   .next()
                   .switchIfEmpty(Mono.empty());

         */
    }

    public String findPrefix(String phoneNumber) {
        String minorPhoneNumber =
                phoneNumber.length() > maxPrefixSize ?
                        phoneNumber.substring(0, maxPrefixSize) :
                        phoneNumber;

        Cache cache = cacheManager.getCache(CACHE_NAME);

        var cacheable = cache.get(minorPhoneNumber);

        if (cacheable != null) {
            return (String) cacheable.get();
        }

        var value =
                prefixes.stream()
                        .filter(i -> i.match(minorPhoneNumber))
                        .findFirst().map(i -> i.prefix)
                        .orElse(null);

        cache.put(minorPhoneNumber, value);

        return value;
    }

    public void loadPrefixes() throws Exception {
        var prefixes =
                prefixesConfiguration.getPathType()
                                     .readLines(prefixesConfiguration.getFilePath())
                                     .stream()
                                     .map(PrefixEntry::new)
                                     .collect(Collectors.toList());

        this.maxPrefixSize =
                prefixes.stream()
                        .map(it -> it.prefix.length())
                        .max(Integer::compareTo)
                        .orElse(13);

        this.prefixes = new TreeList<>(prefixes);
    }

    static class PrefixEntry implements Comparable<PrefixEntry> {
        private final String prefix;
        private final AtomicLong hits = new AtomicLong(0);

        PrefixEntry(String prefix) {
            this.prefix = prefix;
        }

        private void increment() {
            this.hits.incrementAndGet();
        }

        @Override
        public int compareTo(PrefixEntry o) {
            return Long.compare(o.hits.longValue(), this.hits.longValue());
        }

        public boolean match(String txt) {
            if (txt != null && txt.startsWith(this.prefix)) {
                this.increment();
                return true;
            }
            return false;
        }
    }

}
