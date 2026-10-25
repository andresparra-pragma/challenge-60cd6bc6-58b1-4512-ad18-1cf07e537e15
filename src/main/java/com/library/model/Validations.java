package com.library.model;

import com.library.exception.InvalidDataException;

/**
 * Validaciones reutilizables que usan las entidades para proteger sus invariantes.
 */
final class Validations {

    private Validations() {
    }

    static String requireNotBlank(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new InvalidDataException("El campo '" + field + "' es obligatorio");
        }
        return value.trim();
    }

    static <T> T requireNotNull(T value, String field) {
        if (value == null) {
            throw new InvalidDataException("El campo '" + field + "' es obligatorio");
        }
        return value;
    }
}
