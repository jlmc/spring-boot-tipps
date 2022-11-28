package io.github.jlmc.pniakotlin.domain.services.blacklist

class ForbiddenInputValuesException(val invalidValues: Collection<String?>) : RuntimeException()
