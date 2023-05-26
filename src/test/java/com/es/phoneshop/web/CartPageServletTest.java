package com.es.phoneshop.web;

import com.es.phoneshop.exceptions.OutOfStockException;
import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.service.CartService;
import com.es.phoneshop.service.ProductService;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.text.ParseException;
import java.util.Locale;
import java.util.Map;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doThrow;

public class CartPageServletTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private ServletConfig servletConfig;

    @Mock
    private RequestDispatcher requestDispatcher;

    @Mock
    private ProductService productService;

    @Mock
    private CartService cartService;

    @Mock
    private HttpSession session;

    @InjectMocks
    private CartPageServlet servlet;

    @Before
    public void setup() throws ServletException {
        MockitoAnnotations.initMocks(this);
        when(servletConfig.getServletContext()).thenReturn(mock(ServletContext.class));
        servlet.init(servletConfig);
        servlet.setCartService(cartService);
        servlet.setProductService(productService);
        when(request.getSession()).thenReturn(session);
    }

    @Test
    public void givenValidCart_whenDoGet_thenReturnCorrectCartPage() throws ServletException, IOException {
        when(request.getRequestDispatcher("/WEB-INF/pages/cart.jsp")).thenReturn(requestDispatcher);

        servlet.doGet(request, response);

        verify(requestDispatcher).forward(request, response);
    }

    @Test
    public void givenValidRequest_whenDoPost_thenUpdateCartAndRedirect() throws ServletException, IOException, OutOfStockException, ParseException {
        String[] productIds = {"1", "2"};
        String[] quantities = {"2", "3"};
        Product product1 = new Product();
        product1.setId(1L);
        Product product2 = new Product();
        product2.setId(2L);
        when(request.getParameterValues("productId")).thenReturn(productIds);
        when(request.getParameterValues("quantity")).thenReturn(quantities);
        when(productService.getProduct(1L)).thenReturn(product1);
        when(productService.getProduct(2L)).thenReturn(product2);
        when(cartService.getCart(request)).thenReturn(new Cart());
        when(request.getContextPath()).thenReturn("/phoneshop");
        when(request.getLocale()).thenReturn(Locale.getDefault());
        when(response.encodeRedirectURL("/phoneshop/cart?message=Cart updated successfully")).thenReturn("/phoneshop/cart?message=Cart%20updated%20successfully");

        servlet.doPost(request, response);

        verify(cartService).updateCart(product1, 2, request);
        verify(cartService).updateCart(product2, 3, request);
        verify(response).sendRedirect(any());
    }

    @Test
    public void givenOutOfStockException_whenDoPost_thenAddsErrorsAndDisplayCartPage() throws ServletException, IOException, OutOfStockException, ParseException {
        String[] productIds = {"1"};
        String[] quantities = {"10"};
        Product product = new Product();
        product.setId(1L);
        OutOfStockException outOfStockException = new OutOfStockException();
        when(request.getParameterValues("productId")).thenReturn(productIds);
        when(request.getParameterValues("quantity")).thenReturn(quantities);
        when(productService.getProduct(1L)).thenReturn(product);
        when(cartService.getCart(request)).thenReturn(new Cart());
        when(request.getContextPath()).thenReturn("/phoneshop");
        when(request.getLocale()).thenReturn(Locale.getDefault());
        doThrow(outOfStockException).when(cartService).updateCart(product, 10, request);
        when(request.getRequestDispatcher("/WEB-INF/pages/cart.jsp")).thenReturn(requestDispatcher);

        servlet.doPost(request, response);

        verify(cartService).updateCart(product, 10, request);
        verify(request).setAttribute(eq("errors"), any(Map.class));
        verify(requestDispatcher).forward(request, response);
    }
}
