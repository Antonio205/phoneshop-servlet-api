package com.es.phoneshop.web;

import com.es.phoneshop.exceptions.OrderNotFoundException;
import com.es.phoneshop.model.order.Order;
import com.es.phoneshop.service.impl.OrderServiceImpl;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class OrderOverviewPageServletTest {

    private OrderOverviewPageServlet servlet;

    @Mock
    private OrderServiceImpl orderService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private ServletConfig config;

    @Mock
    private ServletContext context;

    @Mock
    private RequestDispatcher dispatcher;

    @Before
    public void setup() throws ServletException {
        MockitoAnnotations.initMocks(this);
        servlet = new OrderOverviewPageServlet();
        servlet.init(config);
        servlet.setOrderService(orderService);
        when(request.getServletContext()).thenReturn(context);
        when(context.getRequestDispatcher("/WEB-INF/pages/orderOverview.jsp")).thenReturn(dispatcher);
    }

    @Test
    public void givenValidOrderId_whenDoGet_thenForwardToOrderOverviewPage() throws ServletException, IOException {
        long orderId = 1L;
        Order order = new Order();
        when(request.getPathInfo()).thenReturn("/" + orderId);
        when(request.getRequestDispatcher(any())).thenReturn(dispatcher);
        when(orderService.getOrder(orderId)).thenReturn(order);

        servlet.doGet(request, response);

        verify(request).setAttribute("order", order);
        verify(request).getRequestDispatcher("/WEB-INF/pages/orderOverview.jsp");
        verify(dispatcher).forward(request, response);
    }

    @Test(expected = OrderNotFoundException.class)
    public void givenInvalidOrderId_whenDoGet_thenThrowOrderNotFoundException() throws ServletException, IOException {
        long orderId = 1L;
        when(request.getPathInfo()).thenReturn("/" + orderId);
        when(orderService.getOrder(orderId)).thenThrow(new OrderNotFoundException("Invalid order id"));

        servlet.doGet(request, response);
    }
}
