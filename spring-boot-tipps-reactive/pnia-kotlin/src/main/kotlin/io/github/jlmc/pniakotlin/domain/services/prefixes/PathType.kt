package io.github.jlmc.pniakotlin.domain.services.prefixes

import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.file.Files
import java.nio.file.Path

enum class PathType {

    CLASSPATH {
        override fun readLines(filePath: String): List<String> {
            BufferedReader(
                InputStreamReader(
                    PathType::class.java.getResourceAsStream(filePath)
                )
            ).use {
                return it.readLines()
            }
        }

    },
    SYSTEM {
        override fun readLines(filePath: String): List<String> {
            return Files.readAllLines(Path.of(filePath))
        }
    };

    abstract fun readLines(filePath: String): List<String>

}
