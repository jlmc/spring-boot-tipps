package io.github.jlmc.pniakotlin.domain.services.prefixes

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component
import org.springframework.validation.annotation.Validated
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

@Validated
@Component
@ConfigurationProperties(prefix = "pnia.prefixes")
class PrefixesConfiguration {

    @NotNull @NotBlank
    var filePath: String? = null
    @NotNull
    var pathType: PathType? = PathType.CLASSPATH

}
