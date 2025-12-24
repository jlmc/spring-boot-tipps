package io.github.jlmc.firststep

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class FirststepApplication

fun main(args: Array<String>) {
    //System.setProperty("org.hibernate.orm.jdbc.extract", "trace")
    runApplication<FirststepApplication>(*args)
}
