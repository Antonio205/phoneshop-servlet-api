package com.es.phoneshop.web;

import com.es.phoneshop.exceptions.OutOfStockException;
import com.es.phoneshop.validator.CartQuantityValidator;
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
import java.util.HashMap;
import java.util.Map;

public class CartPageServlet extends HttpServlet {

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
        request.getRequestDispatcher("/WEB-INF/pages/cart.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String[] productIds = request.getParameterValues("productId");
        String[] quantities = request.getParameterValues("quantity");
        Map<Long, String> errors = new HashMap<>();

        if (productIds != null) {
            updatingOfCart(productIds, quantities, request, errors);

            if (errors.isEmpty()) {
                response.sendRedirect(request.getContextPath() + "/cart?message=Cart updated successfully");
            } else {
                request.setAttribute("errors", errors);
                doGet(request, response);
            }
        } else {
            response.sendRedirect(request.getContextPath() + "/cart");
        }
    }

    private void updatingOfCart(String[] productIds, String[] quantities, HttpServletRequest request, Map<Long, String> errors) {
        for (int i = 0; i < productIds.length; ++i) {
            long productId = Long.parseLong(productIds[i]);

            String quantityInput = quantities[i];
            CartQuantityValidator cartQuantityValidator = CartQuantityValidator.getInstance();
            if (!cartQuantityValidator.validateQuantityFormat(quantityInput)) {
                errors.put(productId, "Not a number");
                continue;
            }

            int quantity = Integer.parseInt(quantityInput);
            try {
                Product product = productService.getProduct(productId);
                cartService.updateCart(product, quantity, request);
            } catch (OutOfStockException ex) {
                if (ex.getStockRequested() <= 0) {
                    errors.put(productId, "Can't be negative or zero");
                } else {
                    errors.put(productId, "Out of stock, available " + ex.getStockAvailable());
                }
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
