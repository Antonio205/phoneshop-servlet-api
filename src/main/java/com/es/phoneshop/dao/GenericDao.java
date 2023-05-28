package com.es.phoneshop.dao;

import java.util.Optional;

public interface GenericDao<T> {

    Optional<T> getItem(long id);

    void save(T item);
}
