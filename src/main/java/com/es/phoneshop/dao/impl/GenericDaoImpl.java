package com.es.phoneshop.dao.impl;

import com.es.phoneshop.dao.GenericDao;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public abstract class GenericDaoImpl<T> implements GenericDao<T> {

    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    protected List<T> items;

    public GenericDaoImpl() {
        this.items = new ArrayList<>();
    }

    @Override
    public Optional<T> getItem(long id) {
        lock.readLock().lock();
        try {
            return items.stream()
                    .filter(item -> id == getId(item))
                    .findAny();
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public void save(T item) {
        lock.writeLock().lock();
        try {
            if (items.contains(item)) {
                int index = items.indexOf(item);
                items.set(index, item);
            } else {
                items.add(item);
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    public List<T> getItems() {
        return items;
    }

    public void setItems(List<T> items) {
        this.items = items;
    }

    protected long getId(T item) {
        return 0;
    }

}
