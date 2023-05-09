package com.es.phoneshop.web;

import com.es.phoneshop.dao.ProductDao;
import com.es.phoneshop.exceptions.ProductNotFoundException;
import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.service.ProductService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

public class ProductDetailsPageServletTest {

    @Mock
    private ProductService productService;

    @Mock
    private ProductDao productDao;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private ServletConfig config;

    @Mock
    private RequestDispatcher requestDispatcher;

    private ProductDetailsPageServlet servlet;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        servlet = new ProductDetailsPageServlet();
        when(request.getRequestDispatcher(anyString())).thenReturn(requestDispatcher);
        servlet.init(config);
    }

    @Test
    public void givenValidProductId_whenDoGet_thenValidAttributeAndForward() throws Exception {
        long productId = 1L;
        Product product = new Product("sgs", "Samsung Galaxy S", new BigDecimal(100), null, 100, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Samsung/Samsung%20Galaxy%20S.jpg");
        when(productService.getProduct(productId)).thenReturn(product);
        when(request.getPathInfo()).thenReturn("/products/" + productId);

        servlet.setProductService(productService);
        servlet.doGet(request, response);

        verify(request).setAttribute("product", product);
        verify(requestDispatcher).forward(request, response);
    }

    @Test(expected = ProductNotFoundException.class)
    public void givenInvalidProductId_whenDoGet_thenThrowProductNotFoundException() throws Exception {
        String productId = "12345";
        when(request.getPathInfo()).thenReturn("/" + productId);
        when(productService.getProduct(Long.parseLong(productId))).thenReturn(null);

        servlet.doGet(request, response);
    }
}
