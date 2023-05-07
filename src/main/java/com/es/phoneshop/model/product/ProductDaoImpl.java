package com.es.phoneshop.model.product;

import com.es.phoneshop.exceptions.ProductNotFoundException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Optional;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

public class ProductDaoImpl implements ProductDao {

    private static ProductDaoImpl instance;
    private List<Product> products;
    private final ReentrantReadWriteLock productsLock = new ReentrantReadWriteLock();

    private ProductDaoImpl () {
        this.products = new ArrayList<>();
    }

    public static synchronized ProductDaoImpl getInstance() {
        if (instance == null) {
            instance = new ProductDaoImpl();
        }
        return instance;
    }

    @Override
    public Optional<Product> getProduct(long id) {
        productsLock.readLock().lock();
        try {
            return products.stream()
                    .filter(product -> id == product.getId())
                    .findAny();
        } finally {
            productsLock.readLock().unlock();
        }
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

    @Override
    public List<Product> findProducts(String query, SortField sortField, SortOrder sortOrder) {
        productsLock.readLock().lock();
        try {
            Comparator<Product> comparator = Comparator.comparing(product -> {
                if (SortField.description == sortField){
                    return (Comparable)product.getDescription();
                }
                else if (SortField.price == sortField) {
                    return (Comparable)product.getPrice();
                }
                else {
                    return (Comparable)0;
                }
            });
            comparator = (sortOrder == SortOrder.desc) ? comparator.reversed() : comparator;

            return products.stream()
                    .filter(product -> query == null || Arrays.stream(query.split("\\s+")).anyMatch(word -> product.getDescription()
                            .toLowerCase().contains(word.toLowerCase())))
                    .filter(product -> product.getPrice() != null)
                    .filter(product -> product.getPrice().compareTo(BigDecimal.ZERO) > 0)
                    .filter(product -> product.getStock() > 0)
                    .sorted(Comparator.comparingInt((Product product) -> {
                        if (query != null) {
                            return queryCompare(query, product);
                        }
                        else {
                            return 0;
                        }
                    }))
                    .sorted(comparator)
                    .collect(Collectors.toList());
        } finally {
            productsLock.readLock().unlock();
        }
    }

    @Override
    public void save(Product product) {
        productsLock.writeLock().lock();
        try {
            if (products.contains(product)) {
                int index = products.indexOf(product);
                products.set(index, product);
            } else {
                products.add(product);
            }
        } finally {
            productsLock.writeLock().unlock();
        }
    }

    @Override
    public void delete(long id) throws ProductNotFoundException {
        productsLock.writeLock().lock();
        try {
            products.stream()
                    .filter(product -> id == product.getId())
                    .findAny()
                    .map(product -> products.remove(product))
                    .orElseThrow(ProductNotFoundException::new);
        } finally {
            productsLock.writeLock().unlock();
        }
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }
}
