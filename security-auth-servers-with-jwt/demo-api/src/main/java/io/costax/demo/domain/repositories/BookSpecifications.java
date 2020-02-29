package io.costax.demo.domain.repositories;

import io.costax.demo.domain.model.Book;
import org.springframework.data.jpa.domain.Specification;

public final class BookSpecifications {

    public static Specification<Book> withISBN(final String isbn) {
        return (Specification<Book>) (root, query, criteriaBuilder) -> {

            final var isbnPath = criteriaBuilder.upper(root.get("isbn"));
            final var upper = criteriaBuilder.upper(criteriaBuilder.literal(isbn));

            return criteriaBuilder.equal(isbnPath, upper);
        };
    }

}
