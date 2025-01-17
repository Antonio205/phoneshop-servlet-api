package com.es.phoneshop.service;

import com.es.phoneshop.exceptions.OutOfStockException;
import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.model.product.Product;
import jakarta.servlet.http.HttpServletRequest;

public interface CartService {
    Cart getCart(HttpServletRequest request);

    void addToCart(Product product, int quantity, HttpServletRequest request) throws OutOfStockException;

    void updateCart(Product product, int quantity, HttpServletRequest request) throws OutOfStockException;

    void deleteCartItem(Product product, HttpServletRequest request);

    void clearCart(HttpServletRequest request);
}
