package io.github.jlmc.firststep.adapter.`in`.graphql

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.stereotype.Controller
import java.time.LocalDate
import java.time.OffsetDateTime

@Controller
class ScalarTestResolver {

    private val logger: Logger = LoggerFactory.getLogger(javaClass)

    @QueryMapping
    fun scalarTest(
        @Argument date: LocalDate,
        @Argument bornAt: OffsetDateTime,
    ): String {
        logger.info("Receive scalar type: {}", date)

        return "Receive scalar type $date born at $bornAt"
    }
}
