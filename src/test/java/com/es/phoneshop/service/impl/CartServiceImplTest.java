package com.es.phoneshop.service.impl;

import com.es.phoneshop.exceptions.OutOfStockException;
import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.model.cart.CartItem;
import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.service.ProductService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.any;

public class CartServiceImplTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private Cart cart;

    @Mock
    private CartItem cartItem;

    @Mock
    private HttpSession session;

    private CartServiceImpl cartService;

    @Mock
    private ProductService productService;

    private Product product;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        cartService = CartServiceImpl.getInstance();
        productService = mock(ProductService.class);
        product = new Product("TestProduct", "TestBrand", new BigDecimal(1), null, 10, null);
        when(productService.getProduct(anyLong())).thenReturn(product);
        when(request.getSession()).thenReturn(session);
        when(request.getSession().getAttribute(cartService.getCartSessionAttribute())).thenReturn(cart);
    }

    @Test
    public void givenNotPresentedCart_whenGetCart_thenReturnNull() {
        when(session.getAttribute(cartService.getCartSessionAttribute())).thenReturn(null);

        Cart cart = cartService.getCart(request);

        assertNull(cart);
    }

    @Test
    public void givenPresentedCart_whenGetCart_thenGiveCart() {
        Cart resultCart = cartService.getCart(request);

        assertSame(cart, resultCart);
        verify(request.getSession(), never()).setAttribute(eq(cartService.getCartSessionAttribute()), any(Cart.class));
    }

    @Test
    public void givenCartItems_whenClearCart_thenClearCart() {
        List<CartItem> cartItems = new ArrayList<>();
        cartItems.add(new CartItem(new Product(), 3));
        cartService.getCart(request).setItems(cartItems);

        cartService.clearCart(request);

        assertTrue(cartService.getCart(request).getItems().isEmpty());
    }

    @Test
    public void givenValidProduct_whenAddToCart_thenCorrectAddToCart() throws OutOfStockException {
        Product product = new Product();
        product.setStock(10);
        Cart cart = new Cart();
        when(session.getAttribute(anyString())).thenReturn(cart);

        cartService.addToCart(product, 5, request);

        assertEquals(1, cart.getItems().size());
        CartItem cartItem = cart.getItems().get(0);
        assertEquals(product, cartItem.getProduct());
        assertEquals(5, cartItem.getQuantity());
    }

    @Test(expected = OutOfStockException.class)
    public void givenInvalidStockCart_whenAdd_thenThrowOutOfStockException() throws OutOfStockException {
        product.setStock(5);
        int quantity = 10;

        cartService.addToCart(productService.getProduct(1L), quantity, request);
    }

    @Test
    public void givenCartItems_whenDeleteCart_thenRemovesCartItemFromCart() {
        List<CartItem> cartItems = new ArrayList<>();
        cartItems.add(cartItem);
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("cart")).thenReturn(cart);
        when(cart.getItems()).thenReturn(cartItems);
        when(cartItem.getProduct()).thenReturn(product);

        cartService.deleteCartItem(product, request);

        assertFalse(cartService.getCart(request).getItems().contains(cartItem));
    }

    @Test
    public void givenExistingCartItem_whenUpdateCart_thenUpdatesCartItemQuantity() throws OutOfStockException {
        List<CartItem> cartItems = new ArrayList<>();
        cartItems.add(cartItem);
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("cart")).thenReturn(cart);
        when(cart.getItems()).thenReturn(cartItems);
        when(cartItem.getProduct()).thenReturn(product);
        product.setStock(10);

        cartService.updateCart(product, 5, request);

        assertTrue(cartService.getCart(request).getItems().contains(cartItem));
    }

    @Test
    public void givenNonExistingCartItem_whenUpdateCart_thenAddsNewCartItemToCart() throws OutOfStockException {
        List<CartItem> cartItems = new ArrayList<>();
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("cart")).thenReturn(cart);
        when(cart.getItems()).thenReturn(cartItems);
        product.setStock(10);

        cartService.updateCart(product, 5, request);

        assertFalse(cartService.getCart(request).getItems().contains(cartItem));
    }

    @Test(expected = OutOfStockException.class)
    public void givenOutOfStockException_whenUpdateCart_thenThrowsException() throws OutOfStockException {
        product.setStock(5);

        cartService.updateCart(product, 10, request);
    }
}

