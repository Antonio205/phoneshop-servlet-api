package com.es.phoneshop.dao.impl;

import com.es.phoneshop.exceptions.ProductNotFoundException;
import com.es.phoneshop.model.order.Order;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;


public class OrderDaoImplTest {

    private OrderDaoImpl orderDao;

    @Before
    public void setup() {
        orderDao = OrderDaoImpl.getInstance();
    }

    @Test
    public void givenNonExistingId_whenGetItem_thenReturnEmptyOptional() {
        long nonExistingId = 999L;

        Optional<Order> result = orderDao.getItem(nonExistingId);

        assertFalse(result.isPresent());
    }

    @Test
    public void givenExistingId_whenGetItem_thenReturnOrder() {
        long existingId = 200L;
        Order order = new Order();
        order.setId(existingId);
        orderDao.getItems().add(order);

        Optional<Order> result = orderDao.getItem(existingId);

        assertTrue(result.isPresent());
        assertEquals(result.get(), order);
    }

    @Test
    public void givenNewOrder_whenSave_thenAddToItems() {
        Order newOrder = new Order();
        newOrder.setId(100L);

        orderDao.save(newOrder);

        List<Order> items = orderDao.getItems();
        assertTrue(items.contains(newOrder));
    }

    @Test(expected = NullPointerException.class)
    public void givenNullOrder_whenSave_thenNotSaveNullOrder() {
        Order order = null;

        orderDao.save(order);

        assertFalse(orderDao.getItems().contains(order));
        orderDao.getItems().clear();
    }

}
