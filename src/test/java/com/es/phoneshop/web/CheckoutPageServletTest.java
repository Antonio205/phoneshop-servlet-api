package com.es.phoneshop.web;

import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.model.cart.CartItem;
import com.es.phoneshop.model.order.Order;
import com.es.phoneshop.service.CartService;
import com.es.phoneshop.service.OrderService;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CheckoutPageServletTest {

    private CheckoutPageServlet servlet;

    @Mock
    private OrderService orderService;

    @Mock
    private CartService cartService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private ServletConfig config;

    @Mock
    private ServletContext context;

    @Mock
    private HttpSession session;

    @Mock
    private Cart cart;

    @Mock
    private Order order;

    @Mock
    private RequestDispatcher dispatcher;

    @Mock
    private List<CartItem> items;

    @Before
    public void setup() throws ServletException {
        MockitoAnnotations.initMocks(this);
        servlet = new CheckoutPageServlet();
        servlet.init(config);
        servlet.setCartService(cartService);
        servlet.setOrderService(orderService);
        when(request.getServletContext()).thenReturn(context);
        when(request.getSession()).thenReturn(session);
        when(context.getRequestDispatcher("/WEB-INF/pages/checkout.jsp")).thenReturn(dispatcher);
        when(request.getRequestDispatcher(any())).thenReturn(dispatcher);
        when(cartService.getCart(request)).thenReturn(cart);
        when(cart.getItems()).thenReturn(items);
    }

    @Test
    public void givenCartNotEmpty_whenDoGet_thenForwardToCheckoutPage() throws ServletException, IOException {
        when(orderService.createOrder(cart)).thenReturn(order);
        when(items.isEmpty()).thenReturn(false);

        servlet.doGet(request, response);

        verify(request).setAttribute(eq("order"), any(Order.class));
        verify(request).setAttribute(eq("paymentMethods"), any());
        verify(request).getRequestDispatcher("/WEB-INF/pages/checkout.jsp");
        verify(dispatcher).forward(request, response);
    }

    @Test
    public void givenCartEmpty_whenDoGet_thenRedirectToCartPageWithError() throws ServletException, IOException {
        when(items.isEmpty()).thenReturn(true);

        servlet.doGet(request, response);

        verify(response).sendRedirect(request.getContextPath() + "/cart?checkoutError=You can't checkout when your cart is empty");
    }

    @Test
    public void givenValidOrder_whenDoPost_thenPlaceOrderAndRedirectToOrderOverviewPage() throws ServletException, IOException {
        when(cartService.getCart(request)).thenReturn(cart);
        when(orderService.createOrder(cart)).thenReturn(order);
        when(request.getParameter("firstName")).thenReturn("gertgr");
        when(request.getParameter("lastName")).thenReturn("regregre");
        when(request.getParameter("phone")).thenReturn("375123456789");
        when(request.getParameter("deliveryDate")).thenReturn("2023-05-17");
        when(request.getParameter("deliveryAddress")).thenReturn("regrege");
        when(request.getParameter("paymentMethod")).thenReturn("CREDIT_CARD");

        servlet.doPost(request, response);

        verify(orderService).placeOrder(order);
        verify(cartService).clearCart(request);
        verify(response).sendRedirect(request.getContextPath() + "/order/overview/" + order.getId());
    }

    @Test
    public void givenInvalidOrder_whenDoPost_thenForwardToCheckoutPageWithError() throws ServletException, IOException {
        Map<String, String> errors = new HashMap<>();
        errors.put("firstName", "Value is required");
        when(cartService.getCart(request)).thenReturn(cart);
        when(orderService.createOrder(cart)).thenReturn(order);
        when(request.getParameter("firstName")).thenReturn(null);

        servlet.doPost(request, response);

        verify(request).setAttribute("order", order);
        verify(request).setAttribute(eq("paymentMethods"), any());
        verify(request).getRequestDispatcher("/WEB-INF/pages/checkout.jsp");
        verify(dispatcher).forward(request, response);
    }
}
