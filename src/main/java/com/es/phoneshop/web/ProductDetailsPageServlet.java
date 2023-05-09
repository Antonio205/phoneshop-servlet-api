package com.es.phoneshop.web;

import com.es.phoneshop.exceptions.ProductNotFoundException;
import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.service.ProductService;
import com.es.phoneshop.service.ProductServiceImpl;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ProductDetailsPageServlet extends HttpServlet {

    private ProductService productService;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        productService = ProductServiceImpl.getInstance();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String productId = request.getPathInfo();
        while (productId != null && productId.contains("/")) {
            productId = productId.substring(productId.indexOf('/') + 1);
        }

        long id = Long.parseLong(productId);
        Product productOptional = productService.getProduct(id);
        if (productOptional != null) {
            Product product = productOptional;
            request.setAttribute("product", product);
            request.getRequestDispatcher("/WEB-INF/pages/product.jsp").forward(request, response);
        } else {
            throw new ProductNotFoundException();
        }
    }

    public ProductService getProductService() {
        return productService;
    }

    public void setProductService(ProductService productService) {
        this.productService = productService;
    }
}
