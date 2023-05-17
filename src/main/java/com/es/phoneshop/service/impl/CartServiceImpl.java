package com.es.phoneshop.service.impl;

import com.es.phoneshop.exceptions.OutOfStockException;
import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.model.cart.CartItem;
import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.service.CartService;
import com.es.phoneshop.service.ProductService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.util.Optional;

public class CartServiceImpl implements CartService {

    private final String CART_SESSION_ATTRIBUTE = CartServiceImpl.class.getName() + ".cart";
    private static CartServiceImpl instance;
    private ProductService productService;

    private CartServiceImpl() {
        productService = ProductServiceImpl.getInstance();
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
    public void addToCart(Cart cart, Product product, int quantity) throws OutOfStockException {
        synchronized (cart) {
            Optional<CartItem> existingCartItem = cart.getItems()
                    .stream()
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
        }
    }

    public void setProductService(ProductService productService) {
        this.productService = productService;
    }

    public String getCartSessionAttribute() {
        return CART_SESSION_ATTRIBUTE;
    }
}
