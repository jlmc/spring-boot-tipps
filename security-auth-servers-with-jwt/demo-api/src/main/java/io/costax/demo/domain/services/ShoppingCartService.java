package io.costax.demo.domain.services;

import io.costax.demo.domain.exceptions.BadResourceIdException;
import io.costax.demo.domain.exceptions.ResourceInUseException;
import io.costax.demo.domain.model.Book;
import io.costax.demo.domain.model.ShoppingCart;
import io.costax.demo.domain.model.User;
import io.costax.demo.domain.repositories.BookRepository;
import io.costax.demo.domain.repositories.ShoppingCartRepository;
import io.costax.demo.domain.repositories.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ShoppingCartService {

    private final ShoppingCartRepository shoppingCartRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;

    public ShoppingCartService(final ShoppingCartRepository shoppingCartRepository,
                               final UserRepository userRepository,
                               final BookRepository bookRepository) {
        this.shoppingCartRepository = shoppingCartRepository;
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
    }

    @Transactional
    public void addItem(Integer userId, Integer bookId, Integer qty) {
        final User user = userRepository.findById(userId).orElseThrow(() -> BadResourceIdException.of(User.class, userId));
        final Book book = bookRepository.findById(bookId).orElseThrow(() -> BadResourceIdException.of(Book.class, bookId));

        final ShoppingCart shoppingCart = shoppingCartRepository.findById(userId).orElseGet(() -> ShoppingCart.of(user));
        shoppingCart.addItem(book, qty);

        shoppingCartRepository.save(shoppingCart);
    }
}
