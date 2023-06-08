package com.es.phoneshop.dao;

import com.es.phoneshop.exceptions.ProductNotFoundException;
import com.es.phoneshop.model.product.Product;

import java.math.BigDecimal;
import java.util.List;

public interface ProductDao extends GenericDao<Product> {

    List<Product> findProducts(String query, String sortField, String sortOrder);

    List<Product> findProducts(String query, BigDecimal minPrice, BigDecimal maxPrice, String wordType);

    List<Product> findProducts(String query);

    List<Product> findProducts();

    void delete(long id) throws ProductNotFoundException;
}
