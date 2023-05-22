package com.es.phoneshop.web;

import com.es.phoneshop.exceptions.OutOfStockException;
import com.es.phoneshop.model.cart.Cart;
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
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.anyLong;

public class AddCartServletTest {

    @Mock
    private ProductService productService;

    @Mock
    private CartService cartService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private ServletConfig config;

    @Mock
    private HttpSession session;

    @Mock
    private HttpServletResponse response;

    private AddCartServlet servlet;

    @Before
    public void setup() throws ServletException {
        MockitoAnnotations.initMocks(this);
        servlet = new AddCartServlet();
        servlet.init(config);
        servlet.setProductService(productService);
        servlet.setCartService(cartService);
        when(request.getSession()).thenReturn(session);
    }

    @Test
    public void givenValidRequest_whenDoPost_thenAddProductToCart() throws ServletException, IOException, OutOfStockException, ParseException {
        String productId = "1";
        String quantity = "2";
        Product product = new Product();
        product.setId(1L);
        Cart cart = new Cart();
        NumberFormat numberFormat = NumberFormat.getInstance(Locale.getDefault());

        when(request.getPathInfo()).thenReturn("/" + productId);
        when(productService.getProduct(anyLong())).thenReturn(product);
        when(request.getParameter("quantity")).thenReturn(quantity);
        when(cartService.getCart(request)).thenReturn(cart);
        when(request.getLocale()).thenReturn(Locale.UK);

        servlet.doPost(request, response);

        verify(productService, times(1)).getProduct(1L);
        verify(cartService, times(1)).addToCart(product, numberFormat.parse(quantity).intValue(), request);
        verify(response, times(1)).sendRedirect(any());
    }
}
