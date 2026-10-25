package com.library.model;

/**
 * Contrato común para las entidades que se pueden identificar de forma única.
 *
 * @param <ID> tipo del identificador
 */
public interface Identifiable<ID> {
    ID getId();
}
