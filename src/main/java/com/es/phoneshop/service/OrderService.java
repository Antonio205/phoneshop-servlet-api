package com.es.phoneshop.service;

import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.model.order.Order;
import com.es.phoneshop.model.order.PaymentMethod;

import java.util.List;

public interface OrderService {
    Order getOrder(long id);

    Order createOrder(Cart cart);

    void placeOrder(Order order);

    List<PaymentMethod> getPaymentMethods();
}
