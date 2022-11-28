package io.github.jlmc.pniakotlin

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class PniaKotlinApplication

fun main(args: Array<String>) {
    runApplication<PniaKotlinApplication>(*args)
}
