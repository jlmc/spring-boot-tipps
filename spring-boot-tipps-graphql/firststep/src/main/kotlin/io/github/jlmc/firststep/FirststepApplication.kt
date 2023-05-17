package io.github.jlmc.firststep

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class FirststepApplication

fun main(args: Array<String>) {
    runApplication<FirststepApplication>(*args)
}
