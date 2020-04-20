package com.cheapflights.tickets.service;

import java.util.Collection;
import java.util.Optional;

public interface CrudService<T> {

    Optional<T> findById(Long id);

    Collection<T> findAll();

}
