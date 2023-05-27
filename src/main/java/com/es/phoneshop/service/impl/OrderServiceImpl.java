package com.es.phoneshop.service.impl;

import com.es.phoneshop.dao.OrderDao;
import com.es.phoneshop.dao.impl.OrderDaoImpl;
import com.es.phoneshop.exceptions.OrderNotFoundException;
import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.model.cart.CartItem;
import com.es.phoneshop.model.order.Order;
import com.es.phoneshop.model.order.PaymentMethod;
import com.es.phoneshop.service.OrderService;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

public class OrderServiceImpl implements OrderService {

    private static OrderServiceImpl instance;
    private OrderDaoImpl orderDao;

    private OrderServiceImpl() {
        orderDao = OrderDaoImpl.getInstance();
    }

    public static synchronized OrderServiceImpl getInstance() {
        if (instance == null) {
            instance = new OrderServiceImpl();
        }
        return instance;
    }

    @Override
    public Order getOrder(long id) {
        return orderDao.getItem(id).orElseThrow(() ->
                new OrderNotFoundException("Invalid id " + id + " while getting order"));
    }

    @Override
    public Order createOrder(Cart cart) {
        Order order = new Order();
        order.setItems(cart.getItems().stream().map(CartItem::clone).toList());
        order.setSubtotal(cart.getTotalCost());
        order.setDeliveryCost(calculateDeliveryCost());
        order.setTotalCost(order.getSubtotal().add(order.getDeliveryCost()));

        return order;
    }

    @Override
    public void placeOrder(Order order) {
        orderDao.save(order);
    }

    @Override
    public List<PaymentMethod> getPaymentMethods() {
        return Arrays.asList(PaymentMethod.values());
    }

    private BigDecimal calculateDeliveryCost() {
        return new BigDecimal(5);
    }

    public void setOrderDao(OrderDao orderDao) {
        this.orderDao = (OrderDaoImpl) orderDao;
    }
}
