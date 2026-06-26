package org.yearup.service;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.yearup.models.*;
import org.yearup.repository.OrderRepository;

import java.time.LocalDateTime;

@Service
public class OrderService
{
    private final OrderRepository orderRepository;
    private final ShoppingCartService shoppingCartService;
    private final ProfileService profileService;

    public OrderService(OrderRepository orderRepository,
                        ShoppingCartService shoppingCartService,
                        ProfileService profileService)
    {
        this.orderRepository = orderRepository;
        this.shoppingCartService = shoppingCartService;
        this.profileService = profileService;
    }

    @Transactional
    public Order checkout(int userId)
    {
        ShoppingCart cart = shoppingCartService.getByUserId(userId);
        Profile profile = profileService.getByUserId(userId);

        Order order = new Order();
        order.setUserId(userId);
        order.setDate(LocalDateTime.now());
        order.setAddress(profile != null ? profile.getAddress() : "");
        order.setCity(profile != null ? profile.getCity() : "");
        order.setState(profile != null ? profile.getState() : "");
        order.setZip(profile != null ? profile.getZip() : "");

        Order saved = orderRepository.save(order);

        for (ShoppingCartItem cartItem : cart.getItems().values())
        {
            OrderLineItem lineItem = new OrderLineItem();
            lineItem.setOrderId(saved.getOrderId());
            lineItem.setProductId(cartItem.getProductId());
            lineItem.setSalesPrice(cartItem.getProduct().getPrice());
            lineItem.setQuantity(cartItem.getQuantity());
            lineItem.setDiscount(cartItem.getDiscountPercent());
            saved.getLineItems().add(lineItem);
        }

        Order result = orderRepository.save(saved);
        shoppingCartService.clearCart(userId);
        return result;
    }
}
