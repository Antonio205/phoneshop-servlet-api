package com.es.phoneshop.service.impl;

import com.es.phoneshop.dao.impl.OrderDaoImpl;
import com.es.phoneshop.exceptions.OrderNotFoundException;
import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.model.cart.CartItem;
import com.es.phoneshop.model.order.Order;
import com.es.phoneshop.model.order.PaymentMethod;
import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.service.OrderService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OrderServiceImplTest {

    private OrderService orderService;
    private OrderDaoImpl orderDao;

    @Before
    public void setup() {
        orderDao = mock(OrderDaoImpl.class);
        orderService = OrderServiceImpl.getInstance();
        ((OrderServiceImpl) orderService).setOrderDao(orderDao);
    }

    @Test
    public void givenExistingOrderId_whenGetOrder_thenReturnOrder() {
        long orderId = 1L;
        Order expectedOrder = new Order();
        expectedOrder.setId(orderId);
        when(orderDao.getItem(orderId)).thenReturn(Optional.of(expectedOrder));

        Order actualOrder = orderService.getOrder(orderId);

        assertEquals(expectedOrder, actualOrder);
    }

    @Test(expected = OrderNotFoundException.class)
    public void givenNonExistingOrderId_whenGetOrder_thenThrowOrderNotFoundException() {
        long orderId = 1L;
        when(orderDao.getItem(orderId)).thenReturn(java.util.Optional.empty());

        orderService.getOrder(orderId);
    }

    @Test
    public void givenCart_whenCreateOrder_thenReturnOrder() {
        Cart cart = new Cart();
        Product product = new Product();
        product.setPrice(new BigDecimal(1));
        cart.getItems().add(new CartItem(product, 1));
        cart.setTotalCost(new BigDecimal(1));

        Order order = orderService.createOrder(cart);

        assertNotNull(order);
        assertEquals(1, order.getItems().size());
        assertNotNull(order.getSubtotal());
        assertNotNull(order.getDeliveryCost());
        assertNotNull(order.getTotalCost());
    }

    @Test
    public void givenOrder_whenPlaceOrder_thenSaveOrder() {
        Order order = new Order();

        orderService.placeOrder(order);

        Mockito.verify(orderDao, Mockito.times(1)).save(order);
    }

    @Test
    public void givenPaymentMethods_whenGetPaymentMethods_thenReturnListOfPaymentMethods() {
        List<PaymentMethod> expectedPaymentMethods = Arrays.asList(PaymentMethod.values());

        List<PaymentMethod> actualPaymentMethods = orderService.getPaymentMethods();

        assertEquals(expectedPaymentMethods, actualPaymentMethods);
    }
}