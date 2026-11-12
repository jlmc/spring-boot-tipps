package io.github.jlmc.firststep.config

import graphql.scalars.ExtendedScalars
import graphql.schema.idl.RuntimeWiring
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.graphql.execution.RuntimeWiringConfigurer
import java.util.regex.Pattern


@Configuration
class ScalarsConfig {

    @Bean
    fun runtimeWiringConfigurer(): RuntimeWiringConfigurer? {
        return RuntimeWiringConfigurer { wiringBuilder: RuntimeWiring.Builder ->
            wiringBuilder
                .scalar(ExtendedScalars.DateTime)
                .scalar(ExtendedScalars.Date)
                .scalar(
                    ExtendedScalars.newRegexScalar("PhoneNumber")
                        .addPattern(Pattern.compile("^(\\s+)?(\\+|00)?((\\d(\\s+)?){7,12}|(\\d(\\s+)?){3})$"))
                        .build()
                )
        }
    }
}
