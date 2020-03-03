package io.costax.demo.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Data
@Entity
@Table(name = "t_shopping_cart")
public class ShoppingCart {

    @JsonIgnore
    @Id
    private Integer id;

    @JsonIgnore
    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;


    @OneToMany(
            fetch = FetchType.LAZY,
            orphanRemoval = true,
            cascade = CascadeType.ALL

    )
    @JoinColumn(name = "shopping_cart_id")
    private List<ShoppingCartItem> items = new ArrayList<>();

    public static ShoppingCart of(User user) {
        final ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUser(user);

        return shoppingCart;
    }

    public void addItem(final Book book, final Integer qty) {
        items.stream().filter(i -> Objects.equals(i.getBook(), book))
                .findFirst()
                .ifPresentOrElse(si -> si.plusQty(qty),
                () -> items.add(ShoppingCartItem.of(book, qty)));

    }
}
