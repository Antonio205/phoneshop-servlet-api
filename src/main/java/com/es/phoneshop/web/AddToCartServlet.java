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
import java.util.Map;

public class AddToCartServlet extends HttpServlet {

    private ProductService productService;

    private ProductListPageServlet productListPageServlet;

    private CartService cartService;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        productService = ProductServiceImpl.getInstance();
        cartService = CartServiceImpl.getInstance();
        productListPageServlet = new ProductListPageServlet();
        productListPageServlet.init(config);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String productId = request.getPathInfo();
        if (productId != null && productId.contains("/")) {
            productId = productId.substring(productId.indexOf('/') + 1);
        }
        Product product = productService.getProduct(Long.parseLong(productId));
        String quantity = request.getParameter("quantity");
        long currentId = product.getId();

        Map<Long, String> errors = new HashMap<>();
        try {
            NumberFormat format = NumberFormat.getInstance(request.getLocale());
            cartService.addToCart(product, format.parse(quantity).intValue(), request);
        } catch (OutOfStockException ex) {
            if (ex.getStockRequested() <= 0) {
                errors.put(currentId, "Can't be negative or zero");
            } else {
                errors.put(currentId, "Out of stock, available " + ex.getStockAvailable());
            }
        } catch (ParseException ex) {
            errors.put(currentId, "Not a number");
        }

        if (errors.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/products?message=Cart item added successfully");
        } else {
            request.setAttribute("errors", errors);
            productListPageServlet.doGet(request, response);
        }
    }

    public void setProductService(ProductService productService) {
        this.productService = productService;
    }

    public void setCartService(CartService cartService) {
        this.cartService = cartService;
    }
}