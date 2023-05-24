package com.es.phoneshop.service.impl;

import com.es.phoneshop.exceptions.OutOfStockException;
import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.model.cart.CartItem;
import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.service.CartService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.math.BigDecimal;
import java.util.Optional;

public class CartServiceImpl implements CartService {

    private static CartServiceImpl instance;
    private final String CART_SESSION_ATTRIBUTE = "cart";

    private CartServiceImpl() {

    }

    public static synchronized CartServiceImpl getInstance() {
        if (instance == null) {
            instance = new CartServiceImpl();
        }
        return instance;
    }

    @Override
    public Cart getCart(HttpServletRequest request) {
        HttpSession session = request.getSession();
        synchronized (session) {
            return (Cart) session.getAttribute(CART_SESSION_ATTRIBUTE);
        }
    }

    @Override
    public void addToCart(Product product, int quantity, HttpServletRequest request) throws OutOfStockException {
        HttpSession session = request.getSession();
        synchronized (session) {
            Cart cart = (Cart) session.getAttribute(CART_SESSION_ATTRIBUTE);
            Optional<CartItem> existingCartItem = cart.getItems().stream()
                    .filter(item -> item.getProduct().equals(product))
                    .findAny();

            if (existingCartItem.isPresent()) {
                CartItem cartItem = existingCartItem.get();
                int currentQuantity = quantity + cartItem.getQuantity();
                if (product.getStock() < currentQuantity) {
                    throw new OutOfStockException(product, quantity, product.getStock() - cartItem.getQuantity());
                }
                cartItem.setQuantity(currentQuantity);
            } else {
                if (product.getStock() < quantity) {
                    throw new OutOfStockException(product, quantity, product.getStock());
                }
                cart.getItems().add(new CartItem(product, quantity));
            }
            recalculateCart(cart);
        }
    }

    @Override
    public void updateCart(Product product, int quantity, HttpServletRequest request) throws OutOfStockException {
        HttpSession session = request.getSession();
        synchronized (session) {
            Cart cart = (Cart) session.getAttribute(CART_SESSION_ATTRIBUTE);
            Optional<CartItem> existingCartItem = cart.getItems().stream()
                    .filter(item -> item.getProduct().equals(product))
                    .findAny();
            if (product.getStock() < quantity) {
                throw new OutOfStockException(product, quantity, product.getStock());
            }

            if (existingCartItem.isPresent()) {
                CartItem cartItem = existingCartItem.get();
                cartItem.setQuantity(quantity);
            } else {
                cart.getItems().add(new CartItem(product, quantity));
            }
            recalculateCart(cart);
        }
    }

    @Override
    public void deleteCart(Product product, HttpServletRequest request) {
        HttpSession session = request.getSession();
        synchronized (session) {
            Cart cart = (Cart) session.getAttribute(CART_SESSION_ATTRIBUTE);
            cart.getItems().removeIf(cartItem -> cartItem.getProduct().equals(product));
            recalculateCart(cart);
        }
    }

    protected void recalculateCart(Cart cart) {
        cart.setTotalQuantity(cart.getItems().stream()
                .map(CartItem::getQuantity)
                .mapToInt(q -> q).sum());

        cart.setTotalCost(cart.getItems().stream()
                .map(item -> item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add));
    }

    public String getCartSessionAttribute() {
        return CART_SESSION_ATTRIBUTE;
    }
}
