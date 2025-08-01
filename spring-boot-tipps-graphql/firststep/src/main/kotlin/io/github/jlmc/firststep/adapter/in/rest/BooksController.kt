package io.github.jlmc.firststep.adapter.`in`.rest

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(path = ["/books"])
class BooksController {

    data class BookResource(val id: Long, val title: String)

    @GetMapping
    fun books(): List<BookResource> {
        return listOf(BookResource(1L, "book 1"), BookResource(2L, "book 2"), BookResource(3L, "book 3"))
    }
}
