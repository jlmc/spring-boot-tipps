package io.github.jlmc.pniakotlin.domain.services.blacklist

import org.springframework.stereotype.Service

@Service
class BlackListService {

    private val blackList = setOf("true", "false", "TRUE", "FALSE")

    private fun getForbiddenInputs(inputs: Collection<String?>): List<String?> {
        return inputs.filter { it.isNullOrBlank() || blackList.contains(it) }
    }

    fun validate(inputs: Collection<String?>) {
        val forbiddenInputs = getForbiddenInputs(inputs)
        if (forbiddenInputs.isNotEmpty()) {
            throw ForbiddenInputValuesException(forbiddenInputs)
        }
    }

}
