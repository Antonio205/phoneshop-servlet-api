package com.es.phoneshop.dao.impl;

import com.es.phoneshop.dao.ProductDao;
import com.es.phoneshop.exceptions.ProductNotFoundException;
import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.sort.SortField;
import com.es.phoneshop.sort.SortOrder;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

public class ProductDaoImpl extends GenericDaoImpl<Product> implements ProductDao {

    private static ProductDaoImpl instance;
    private final ReentrantReadWriteLock productsLock = new ReentrantReadWriteLock();

    private ProductDaoImpl() {

    }

    public static synchronized ProductDaoImpl getInstance() {
        if (instance == null) {
            instance = new ProductDaoImpl();
        }
        return instance;
    }

    private int queryCompare(String query, Product product) {
        String[] words = query.split("\\s+");
        int matches = 0;
        for (String word : words) {
            if (product.getDescription().toLowerCase().contains(word.toLowerCase())) {
                matches++;
            }
        }

        return -matches;
    }

    private Comparator<Product> getProductComparator(String sortField, String sortOrder) {
        Comparator<Product> comparator = null;

        if (sortField != null && !sortField.isEmpty()) {
            SortField field = SortField.valueOf(sortField);
            comparator = switch (field) {
                case description -> Comparator.comparing(Product::getDescription);
                case price -> Comparator.comparing(Product::getPrice);
            };
        }

        if (sortOrder != null) {
            comparator = (SortOrder.valueOf(sortOrder) == SortOrder.desc) ? comparator.reversed() : comparator;
        }

        return comparator;
    }

    @Override
    public List<Product> findProducts(String query, String sortField, String sortOrder) {
        productsLock.readLock().lock();
        try {
            Comparator<Product> comparator = getProductComparator(sortField, sortOrder);

            List<Product> validProducts = findProducts();

            return validProducts.stream()
                    .filter(product -> query == null || Arrays.stream(query.split("\\s+"))
                            .anyMatch(word -> product.getDescription().toLowerCase().contains(word.toLowerCase())))
                    .sorted(Comparator.comparingInt((Product product) -> query != null ? queryCompare(query, product) : 0))
                    .sorted(comparator)
                    .collect(Collectors.toList());
        } finally {
            productsLock.readLock().unlock();
        }
    }

    @Override
    public List<Product> findProducts(String query) {
        productsLock.readLock().lock();
        try {
            List<Product> validProducts = findProducts();

            return validProducts.stream()
                    .filter(product -> query == null || Arrays.stream(query.split("\\s+"))
                            .anyMatch(word -> product.getDescription().toLowerCase().contains(word.toLowerCase())))
                    .sorted(Comparator.comparingInt((Product product) -> query != null ? queryCompare(query, product) : 0))
                    .collect(Collectors.toList());
        } finally {
            productsLock.readLock().unlock();
        }
    }

    @Override
    public List<Product> findProducts() {
        productsLock.readLock().lock();
        try {
            return super.getItems().stream()
                    .filter(product -> product.getPrice() != null)
                    .filter(product -> product.getPrice().compareTo(BigDecimal.ZERO) > 0)
                    .filter(product -> product.getStock() > 0)
                    .collect(Collectors.toList());
        } finally {
            productsLock.readLock().unlock();
        }
    }

    @Override
    public void delete(long id) throws ProductNotFoundException {
        productsLock.writeLock().lock();
        try {
            super.getItems().stream()
                    .filter(product -> id == product.getId())
                    .findAny()
                    .map(product -> super.getItems().remove(product))
                    .orElseThrow(ProductNotFoundException::new);
        } finally {
            productsLock.writeLock().unlock();
        }
    }

    @Override
    public long getId(Product product) {
        return product.getId();
    }
}
