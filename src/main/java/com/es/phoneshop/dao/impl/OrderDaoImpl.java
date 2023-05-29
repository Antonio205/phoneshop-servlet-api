package com.es.phoneshop.dao.impl;

import com.es.phoneshop.dao.OrderDao;
import com.es.phoneshop.model.order.Order;

import java.util.ArrayList;
import java.util.List;

public class OrderDaoImpl extends GenericDaoImpl<Order> implements OrderDao {

    private static OrderDaoImpl instance;

    private OrderDaoImpl() {

    }

    public static synchronized OrderDaoImpl getInstance() {
        if (instance == null) {
            instance = new OrderDaoImpl();
        }
        return instance;
    }

    @Override
    public long getId(Order order) {
        return order.getId();
    }
}
