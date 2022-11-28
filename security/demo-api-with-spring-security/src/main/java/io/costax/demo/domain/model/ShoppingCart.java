package io.costax.demo.domain.model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

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

    public ShoppingCart() {
    }

    public Integer getId() {
        return this.id;
    }

    public User getUser() {
        return this.user;
    }

    public List<ShoppingCartItem> getItems() {
        return this.items;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setItems(List<ShoppingCartItem> items) {
        this.items = items;
    }

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof ShoppingCart)) return false;
        final ShoppingCart other = (ShoppingCart) o;
        if (!other.canEqual((Object) this)) return false;
        final Object this$id = this.getId();
        final Object other$id = other.getId();
        if (this$id == null ? other$id != null : !this$id.equals(other$id)) return false;
        final Object this$user = this.getUser();
        final Object other$user = other.getUser();
        if (this$user == null ? other$user != null : !this$user.equals(other$user)) return false;
        final Object this$items = this.getItems();
        final Object other$items = other.getItems();
        if (this$items == null ? other$items != null : !this$items.equals(other$items)) return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof ShoppingCart;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $id = this.getId();
        result = result * PRIME + ($id == null ? 43 : $id.hashCode());
        final Object $user = this.getUser();
        result = result * PRIME + ($user == null ? 43 : $user.hashCode());
        final Object $items = this.getItems();
        result = result * PRIME + ($items == null ? 43 : $items.hashCode());
        return result;
    }

    public String toString() {
        return "ShoppingCart(id=" + this.getId() + ", user=" + this.getUser() + ", items=" + this.getItems() + ")";
    }
}
