package com.es.phoneshop.web;

import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.service.ProductService;
import com.es.phoneshop.service.impl.ProductServiceImpl;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

public class AdvancedSearchPageServlet extends HttpServlet {

    private ProductService productService;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        productService = ProductServiceImpl.getInstance();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String description = request.getParameter("description");
        BigDecimal minPrice = parseBigDecimal(request.getParameter("minPrice"));
        BigDecimal maxPrice = parseBigDecimal(request.getParameter("maxPrice"));
        String wordType = request.getParameter("wordType");
        String error = null;
        List<Product> products;

        if (description != null) {
            if (minPrice != null && maxPrice != null && minPrice.compareTo(maxPrice) > 0) {
                error = "Minimum price cannot be greater than maximum price";
            }
            products = productService.findProducts(description, minPrice, maxPrice, wordType);
        } else {
            products = productService.findProducts();
        }

        request.setAttribute("products", products);
        request.setAttribute("error", error);
        request.getRequestDispatcher("/WEB-INF/pages/advancedSearch.jsp").forward(request, response);
    }

    private BigDecimal parseBigDecimal(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        return new BigDecimal(value);
    }

    public void setProductService(ProductService productService) {
        this.productService = productService;
    }

}