package com.es.phoneshop.service;

import com.es.phoneshop.exceptions.ProductNotFoundException;
import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.model.cart.CartItem;

import java.util.List;

public interface ProductService {
    Product getProduct(long id);

    List<Product> findProducts();

    List<Product> findProducts(String query);

    List<Product> findProducts(String query, String sortField, String sortOrder);

    void decreaseStock(List<CartItem> items);

    void save(Product product);

    void delete(long id) throws ProductNotFoundException;
}
