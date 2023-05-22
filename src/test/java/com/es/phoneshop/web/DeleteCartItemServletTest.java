package com.es.phoneshop.web;

import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.model.cart.CartItem;
import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.service.CartService;
import com.es.phoneshop.service.ProductService;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

public class DeleteCartItemServletTest {

    private DeleteCartItemServlet servlet;

    @Mock
    private ProductService productService;

    @Mock
    private CartService cartService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpSession session;

    @Mock
    private HttpServletResponse response;

    @Mock
    private ServletConfig config;

    @Before
    public void setup() throws ServletException {
        MockitoAnnotations.initMocks(this);
        servlet = new DeleteCartItemServlet();
        servlet.init(config);
        servlet.setProductService(productService);
        servlet.setCartService(cartService);
        when(request.getSession()).thenReturn(session);
    }

    @Test
    public void givenValidCart_whenDoPost_thenCorrectlyDeleteCart() throws IOException {
        String productId = "1";
        Product product = new Product();
        product.setId(1);

        Cart cart = new Cart();
        CartItem cartItem = new CartItem(product, 1);
        cart.setItems(List.of(cartItem));

        when(request.getPathInfo()).thenReturn("/" + productId);
        when(productService.getProduct(1L)).thenReturn(product);
        when(request.getContextPath()).thenReturn("");
        when(cartService.getCart(any())).thenReturn(cart);

        servlet.doPost(request, response);

        verify(productService, times(1)).getProduct(1L);
        verify(cartService, times(1)).deleteCart(product, request);
        verify(response, times(1)).sendRedirect(request.getContextPath() +
                "/cart?message=Cart item removed successfully");
    }
}
