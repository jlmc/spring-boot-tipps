package io.github.jlmc.pniakotlin.domain.services.prefixes

import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationListener
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.stereotype.Component

@Component
class PrefixesInitializer(
    private val prefixesService: PrefixesService
) :
    ApplicationListener<ContextRefreshedEvent?> {

    companion object {
        private val LOGGER = LoggerFactory.getLogger(PrefixesInitializer::class.java)
    }

    override fun onApplicationEvent(event: ContextRefreshedEvent) {
        try {
            LOGGER.info("Initializing 'prefixes' service...")
            prefixesService.loadPrefixes()
            LOGGER.info("Initialized 'prefixes' service!")
        } catch (e: Exception) {
            LOGGER.error("Unexpected problem happens on 'prefixes' service initialization!")
            throw IllegalStateException(e)
        }
    }
}
