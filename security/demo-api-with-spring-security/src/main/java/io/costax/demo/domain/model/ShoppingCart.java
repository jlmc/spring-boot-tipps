package io.costax.demo.domain.model;

import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "t_shopping_cart")
public class ShoppingCart {

    @Id
    private Integer id;

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

}
