package io.costax.versioningapis.uris.api.v1.model;

import io.costax.versioningapis.uris.domain.model.Book;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookDtoV1 {

    private Integer id;
    private String isbn;
    private String title;


    public static BookDtoV1 from(final Book book) {
        BookDtoV1 dto = new BookDtoV1();

        dto.id = book.getId();
        dto.isbn = book.getIsbn();
        dto.title = book.getTitle();

        return dto;
    }

}
