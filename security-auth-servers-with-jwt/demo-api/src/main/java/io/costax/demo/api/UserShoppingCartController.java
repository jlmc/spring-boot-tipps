package io.costax.demo.api;


import io.costax.demo.api.model.ShoppingCartItemRequest;
import io.costax.demo.domain.exceptions.ResourceNotFoundException;
import io.costax.demo.domain.model.ShoppingCart;
import io.costax.demo.domain.repositories.ShoppingCartRepository;
import io.costax.demo.domain.services.ShoppingCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping(
        value = "users/{userId}/shopping-cart",
        produces = {
                MediaType.APPLICATION_JSON_VALUE
        })
public class UserShoppingCartController {

    @Autowired
    ShoppingCartRepository shoppingCartRepository;

    @Autowired
    ShoppingCartService shoppingCartService;

    @PostAuthorize("hasAuthority('MANAGER_USERS') " +
            "or ( hasAuthority('SCOPE_WRITE') and hasAuthority('CREATE_ORDER') and returnObject.id == @securityHelper.getUserId() )")
    @GetMapping
    public ShoppingCart getByUser(@PathVariable("userId") Integer userId) {
        return shoppingCartRepository
                .getById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(ShoppingCart.class, userId));
    }


    @ResponseStatus(HttpStatus.ACCEPTED)
    @PostMapping(path = "/items")
    public void getShoppingCartRepository(@PathVariable("userId") Integer userId,
                                                            @RequestBody @Valid ShoppingCartItemRequest itemRequest) {
        shoppingCartService.addItem(userId, itemRequest.getBookId(), itemRequest.getQty());
    }
}
