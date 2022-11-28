package io.github.jlmc.pniakotlin.domain.model

import com.fasterxml.jackson.annotation.JsonProperty

data class Sector(
    @JsonProperty("number")
    var number: String? = null,
    @JsonProperty("sector")
    val name: String? = null
)
