package com.library.repository;

import com.library.model.Identifiable;

import java.util.List;
import java.util.Optional;

/**
 * Operaciones de almacenamiento comunes a cualquier entidad identificable.
 *
 * @param <T>  tipo de la entidad
 * @param <ID> tipo del identificador de la entidad
 */
public interface Repository<T extends Identifiable<ID>, ID> {

    T save(T entity);

    Optional<T> findById(ID id);

    List<T> findAll();

    boolean existsById(ID id);
}
