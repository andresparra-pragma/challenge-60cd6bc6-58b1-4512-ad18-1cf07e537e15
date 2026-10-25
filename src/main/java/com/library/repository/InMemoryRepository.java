package com.library.repository;

import com.library.model.Identifiable;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Implementación en memoria reutilizable por cualquier entidad gracias a los genéricos.
 * Conserva el orden de inserción y sincroniza el acceso al mapa.
 */
public abstract class InMemoryRepository<T extends Identifiable<ID>, ID> implements Repository<T, ID> {

    private final Map<ID, T> storage = new LinkedHashMap<>();

    @Override
    public synchronized T save(T entity) {
        storage.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public synchronized Optional<T> findById(ID id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public synchronized List<T> findAll() {
        return List.copyOf(storage.values());
    }

    @Override
    public synchronized boolean existsById(ID id) {
        return storage.containsKey(id);
    }
}
