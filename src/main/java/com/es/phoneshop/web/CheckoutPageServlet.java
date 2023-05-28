package com.es.phoneshop.web;

import com.es.phoneshop.validator.OrderDateValidator;
import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.model.order.Order;
import com.es.phoneshop.model.order.PaymentMethod;
import com.es.phoneshop.service.CartService;
import com.es.phoneshop.service.OrderService;
import com.es.phoneshop.service.impl.CartServiceImpl;
import com.es.phoneshop.service.impl.OrderServiceImpl;
import com.es.phoneshop.validator.OrderPhoneValidator;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class CheckoutPageServlet extends HttpServlet {

    private OrderService orderService;
    private CartService cartService;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        orderService = OrderServiceImpl.getInstance();
        cartService = CartServiceImpl.getInstance();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Cart cart = cartService.getCart(request);
        if (cart.getItems().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/cart?checkoutError=You can't checkout when your cart is empty");
            return;
        }

        request.setAttribute("order", orderService.createOrder(cart));
        request.setAttribute("paymentMethods", orderService.getPaymentMethods());
        request.getRequestDispatcher("/WEB-INF/pages/checkout.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        Cart cart = cartService.getCart(request);
        Order order = orderService.createOrder(cart);
        Map<String, String> errors = new HashMap<>();

        setRequiredValue(request, "firstName", errors, order::setFirstName);
        setRequiredValue(request, "lastName", errors, order::setLastName);
        setPhoneMethod(request, errors, order);
        setDeliveryDate(request, errors, order);
        setRequiredValue(request, "deliveryAddress", errors, order::setDeliveryAddress);
        setPaymentMethod(request, errors, order);

        if (errors.isEmpty()) {
            orderService.placeOrder(order);
            cartService.clearCart(request);
            response.sendRedirect(request.getContextPath() + "/order/overview/" + order.getId());
        } else {
            request.setAttribute("errors", errors);
            request.setAttribute("order", order);
            request.setAttribute("paymentMethods", orderService.getPaymentMethods());
            request.getRequestDispatcher("/WEB-INF/pages/checkout.jsp").forward(request, response);
        }
    }

    private void setDeliveryDate(HttpServletRequest request, Map<String, String> errors, Order order) {
        String value = request.getParameter("deliveryDate");
        OrderDateValidator orderDateValidator = OrderDateValidator.getInstance();

        if (value.isEmpty()) {
            errors.put("deliveryDate", "Value is required");
        } else if (!orderDateValidator.validateDateFormat(value)) {
            errors.put("deliveryDate", "Incorrect date, must be tomorrow or later");
        } else {
            order.setDeliveryDate(value);
        }
    }

    private void setRequiredValue(HttpServletRequest request, String parametr, Map<String, String> errors, Consumer<String> consumer) {
        String value = request.getParameter(parametr);
        if (value.isEmpty()) {
            errors.put(parametr, "Value is required");
        } else {
            consumer.accept(value);
        }
    }

    private void setPhoneMethod(HttpServletRequest request, Map<String, String> errors, Order order) {
        String value = request.getParameter("phone");
        OrderPhoneValidator orderPhoneValidator = OrderPhoneValidator.getInstance();

        if (value.isEmpty()) {
            errors.put("phone", "Value is required");
        } else if (!orderPhoneValidator.validatePhoneFormat(request.getParameter("phone"))) {
            errors.put("phone", "Phone incorrect, must start with 375 or 80");
        } else {
            order.setPhone(value);
        }
    }

    private void setPaymentMethod(HttpServletRequest request, Map<String, String> errors, Order order) {
        String value = request.getParameter("paymentMethod");
        if (value.isEmpty()) {
            errors.put("paymentMethod", "Value is required");
        } else {
            order.setPaymentMethod(PaymentMethod.valueOf(value));
        }
    }

    public void setCartService(CartService cartService) {
        this.cartService = cartService;
    }

    public void setOrderService(OrderService orderService) {
        this.orderService = orderService;
    }
}
