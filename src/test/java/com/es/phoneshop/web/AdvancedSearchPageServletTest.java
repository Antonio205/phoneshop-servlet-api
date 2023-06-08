package com.es.phoneshop.web;

import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.service.ProductService;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AdvancedSearchPageServletTest {

    @Mock
    private ProductService productService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private RequestDispatcher requestDispatcher;

    private AdvancedSearchPageServlet servlet;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        servlet = new AdvancedSearchPageServlet();
        servlet.setProductService(productService);
    }

    @Test
    public void givenDescription_whenDoGet_thenSetProducts() throws Exception {
        String description = "phone";
        BigDecimal minPrice = null;
        BigDecimal maxPrice = null;
        String wordType = "ALL";
        String error = null;
        List<Product> products = new ArrayList<>();

        when(request.getParameter("description")).thenReturn(description);
        when(request.getParameter("minPrice")).thenReturn(null);
        when(request.getParameter("maxPrice")).thenReturn(null);
        when(request.getParameter("wordType")).thenReturn(wordType);

        when(productService.findProducts(description, minPrice, maxPrice, wordType)).thenReturn(products);

        when(request.getRequestDispatcher("/WEB-INF/pages/advancedSearch.jsp")).thenReturn(requestDispatcher);

        servlet.doGet(request, response);

        verify(request).setAttribute("products", products);
        verify(request).setAttribute("error", error);
        verify(requestDispatcher).forward(request, response);
    }

    @Test
    public void givenNoDescription_whenDoGet_thenSetProducts() throws Exception {
        String description = null;
        BigDecimal minPrice = null;
        BigDecimal maxPrice = null;
        String wordType = null;
        String error = null;
        List<Product> products = new ArrayList<>();

        when(request.getParameter("description")).thenReturn(description);
        when(request.getParameter("minPrice")).thenReturn(null);
        when(request.getParameter("maxPrice")).thenReturn(null);
        when(request.getParameter("wordType")).thenReturn(wordType);

        when(productService.findProducts()).thenReturn(products);

        when(request.getRequestDispatcher("/WEB-INF/pages/advancedSearch.jsp")).thenReturn(requestDispatcher);

        servlet.doGet(request, response);

        verify(request).setAttribute("products", products);
        verify(request).setAttribute("error", error);
        verify(requestDispatcher).forward(request, response);
    }
}