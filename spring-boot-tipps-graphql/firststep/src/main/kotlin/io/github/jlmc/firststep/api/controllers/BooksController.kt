package io.github.jlmc.firststep.api.controllers

import io.github.jlmc.firststep.api.models.BookResource
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(path = ["/books"])
class BooksController {
    @GetMapping
    fun books(): List<BookResource> {
        return listOf(BookResource(1L, "book 1"), BookResource(2L, "book 2"), BookResource(3L, "book 3"))
    }
}
