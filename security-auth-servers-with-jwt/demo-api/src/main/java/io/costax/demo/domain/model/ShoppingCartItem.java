package io.costax.demo.domain.model;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Data
@Entity
@Table(name = "t_shopping_cart_item")
public class ShoppingCartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    @Positive
    private int qty = 1;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    protected static ShoppingCartItem of(final Book book, final Integer qty) {
        final ShoppingCartItem shoppingCartItem = new ShoppingCartItem();
        shoppingCartItem.setBook(book);
        shoppingCartItem.setQty(qty);
        return shoppingCartItem;
    }

    protected void plusQty(final Integer qty) {
        this.qty = this.qty + qty;
    }
}
