package com.es.phoneshop.web;

import com.es.phoneshop.exceptions.OutOfStockException;
import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.service.CartService;
import com.es.phoneshop.service.ProductService;
import com.es.phoneshop.service.impl.CartServiceImpl;
import com.es.phoneshop.service.impl.ProductServiceImpl;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddCartServlet extends HttpServlet {

    private ProductService productService;
    private CartService cartService;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        productService = ProductServiceImpl.getInstance();
        cartService = CartServiceImpl.getInstance();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String query = request.getParameter("query");
        String sortField = request.getParameter("sort");
        String sortOrder = request.getParameter("order");

        List<Product> products;

        if (query != null && sortField != null && sortOrder != null) {
            products = productService.findProducts(query, sortField, sortOrder);
        } else if (query != null) {
            products = productService.findProducts(query);
        } else {
            products = productService.findProducts();
        }

        request.setAttribute("products", products);
        request.getRequestDispatcher("/WEB-INF/pages/productList.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String productId = request.getPathInfo();
        if (productId != null && productId.contains("/")) {
            productId = productId.substring(productId.indexOf('/') + 1);
        }
        Product product = productService.getProduct(Long.parseLong(productId));
        String quantity = request.getParameter("quantity");

        Map<Long, String> errors = new HashMap<>();
        try {
            NumberFormat format = NumberFormat.getInstance(request.getLocale());
            cartService.addToCart(product, format.parse(quantity).intValue(), request);
        } catch (OutOfStockException | ParseException ex) {
            handleException(errors, product.getId(), ex);
        }

        if (errors.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/products?message=Cart item added successfully");
        } else {
            request.setAttribute("errors", errors);
            doGet(request, response);
        }
    }

    private void handleException(Map<Long, String> errors, long productId, Exception ex) {
        if (ex.getClass().equals(ParseException.class)) {
            errors.put(productId, "Not a number");
        } else {
            if (((OutOfStockException) ex).getStockRequested() <= 0) {
                errors.put(productId, "Cant be negative or zero");
            } else {
                errors.put(productId, "Out of stock, available " + ((OutOfStockException) ex).getStockAvailable());
            }
        }
    }

    public void setProductService(ProductService productService) {
        this.productService = productService;
    }

    public void setCartService(CartService cartService) {
        this.cartService = cartService;
    }
}
