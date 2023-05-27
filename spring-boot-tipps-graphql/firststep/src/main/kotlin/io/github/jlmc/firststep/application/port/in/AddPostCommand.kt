package io.github.jlmc.firststep.application.port.`in`

import jakarta.validation.ConstraintViolationException
import jakarta.validation.Validation
import jakarta.validation.ValidatorFactory
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.util.UUID


@JvmRecord
data class AddPostCommand(
    @field:NotBlank val title: String,
    @field:NotBlank val description: String,
    @field:NotNull val authorId: UUID
) {

    init {
        val factory: ValidatorFactory = Validation.buildDefaultValidatorFactory()
        val validator = factory.validator

        val violations = validator.validate(this)

        if (violations.isNotEmpty()) {
            throw ConstraintViolationException(violations)
        }
    }

}
