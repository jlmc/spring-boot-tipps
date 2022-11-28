package io.github.jlmc.pniakotlin.domain.services.prefixes

import org.slf4j.LoggerFactory
import org.springframework.cache.CacheManager
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class PrefixesService(
    private val prefixesConfiguration: PrefixesConfiguration,
    private var cacheManager: CacheManager
) {

    companion object {
        private const val CACHE_NAME = "PREFIXES_SERVICE_CACHE"
    }

    private val logger = LoggerFactory.getLogger(PrefixesService::class.java)

    private var prefixes: List<String> = emptyList()
    private var maxPrefixSize = 13

    fun loadPrefixes() {
        val readedPrefixes: List<String> = prefixesConfiguration.pathType?.let { pathType ->
            prefixesConfiguration.filePath?.let { filePath ->
                pathType.readLines(filePath)
            }
        }.orEmpty()

        // for caching proposes
        val maxPrefixLength: Int = readedPrefixes.maxOfOrNull { it.length } ?: 13

        this.prefixes = readedPrefixes
        this.maxPrefixSize = maxPrefixLength
    }

    fun findByPhoneNumber(phoneNumber: String): Mono<String?> {
        return Mono.fromCallable { findPrefix(phoneNumber) }
    }

    private fun findPrefix(phoneNumber: String): String? {
        val minorPhoneNumber =
            if (phoneNumber.length > maxPrefixSize) phoneNumber.substring(0, maxPrefixSize) else phoneNumber

        logger.info("Trying to find prefix of the number <$phoneNumber> with the key <$minorPhoneNumber> ...")

        val cache = cacheManager.getCache(CACHE_NAME)

        val cacheable = cache!![minorPhoneNumber]

        if (cacheable != null) {
            val prefix = cacheable.get() as String?

            logger.info("Found <${prefix != null}> prefix in the cache, for the phone number <$phoneNumber> with the key <$minorPhoneNumber>, the prefix is <${prefix}> ")
            return prefix
        }

        val prefix = prefixes.firstOrNull(minorPhoneNumber::startsWith)

        cache.put(minorPhoneNumber, prefix)

        logger.info("Found <${prefix != null}> prefix out of the cache, for the phone number <$phoneNumber> with the key <$minorPhoneNumber>, the prefix is <${prefix}> ")

        return prefix
    }
}
