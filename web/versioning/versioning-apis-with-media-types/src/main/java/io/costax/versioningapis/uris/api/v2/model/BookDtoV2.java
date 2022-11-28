package io.costax.versioningapis.uris.api.v2.model;

import io.costax.versioningapis.uris.domain.model.Book;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookDtoV2 {

    private Integer id;
    private String isbn;
    private String title;

    // property added in the version 2
    private String description;

    public static BookDtoV2 from(final Book book) {
        BookDtoV2 dto = new BookDtoV2();

        dto.id = book.getId();
        dto.isbn = book.getIsbn();
        dto.title = book.getTitle();
        dto.description = book.getDescription();

        return dto;
    }

}


