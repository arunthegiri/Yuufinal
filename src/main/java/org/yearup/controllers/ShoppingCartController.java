package org.yearup.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.yearup.models.ShoppingCart;
import org.yearup.models.ShoppingCartItem;
import org.yearup.models.User;
import org.yearup.service.ShoppingCartService;
import org.yearup.service.UserService;

import java.security.Principal;

@RestController
@RequestMapping("cart")
@CrossOrigin
@PreAuthorize("isAuthenticated()")
public class ShoppingCartController
{
    private final ShoppingCartService shoppingCartService;
    private final UserService userService;

    public ShoppingCartController(ShoppingCartService shoppingCartService, UserService userService)
    {
        this.shoppingCartService = shoppingCartService;
        this.userService = userService;
    }

    @GetMapping("")
    public ShoppingCart getCart(Principal principal)
    {
        int userId = getUser(principal).getId();
        return shoppingCartService.getByUserId(userId);
    }

    @PostMapping("products/{productId}")
    @ResponseStatus(HttpStatus.CREATED)
    public ShoppingCart addProduct(@PathVariable int productId, Principal principal)
    {
        int userId = getUser(principal).getId();
        return shoppingCartService.addProduct(userId, productId);
    }

    @PutMapping("products/{productId}")
    public ShoppingCart updateProduct(@PathVariable int productId,
                                      @RequestBody ShoppingCartItem item,
                                      Principal principal)
    {
        int userId = getUser(principal).getId();
        return shoppingCartService.updateProduct(userId, productId, item.getQuantity());
    }

    @DeleteMapping("")
    public ShoppingCart clearCart(Principal principal)
    {
        int userId = getUser(principal).getId();
        return shoppingCartService.clearCart(userId);
    }

    private User getUser(Principal principal)
    {
        return userService.getByUserName(principal.getName());
    }
}
