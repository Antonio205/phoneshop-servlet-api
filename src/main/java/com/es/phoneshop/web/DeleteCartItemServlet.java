package com.es.phoneshop.web;

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

public class DeleteCartItemServlet extends HttpServlet {

    private ProductService productService;
    private CartService cartService;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        productService = ProductServiceImpl.getInstance();
        cartService = CartServiceImpl.getInstance();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String productId = request.getPathInfo();
        while (productId != null && productId.contains("/")) {
            productId = productId.substring(productId.indexOf('/') + 1);
        }
        Product product = productService.getProduct(Long.parseLong(productId));

        cartService.deleteCart(product, request);

        response.sendRedirect(request.getContextPath() + "/cart?message=Cart item removed successfully");
    }

    public void setProductService(ProductService productService) {
        this.productService = productService;
    }

    public void setCartService(CartService cartService) {
        this.cartService = cartService;
    }
}
