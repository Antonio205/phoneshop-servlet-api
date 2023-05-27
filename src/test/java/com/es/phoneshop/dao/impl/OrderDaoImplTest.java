package com.es.phoneshop.dao.impl;

import com.es.phoneshop.model.order.Order;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


public class OrderDaoImplTest {

    private OrderDaoImpl orderDao;

    @Before
    public void setup() {
        orderDao = OrderDaoImpl.getInstance();
    }

    @Test
    public void givenNonExistingId_whenGetItem__thenReturnEmptyOptional() {
        long nonExistingId = 999L;
        Optional<Order> result = orderDao.getItem(nonExistingId);
        assertFalse(result.isPresent());
    }

    @Test
    public void givenNewOrder_whenSave_thenAddToItems() {
        Order newOrder = new Order();
        newOrder.setId(100L);

        orderDao.save(newOrder);

        List<Order> items = orderDao.getItems();
        assertTrue(items.contains(newOrder));
    }
}
