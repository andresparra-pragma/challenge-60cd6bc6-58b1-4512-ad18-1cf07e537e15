package com.library.exception;

/** Se intentó registrar un recurso con un identificador que ya existe. */
public class DuplicateResourceException extends LibraryException {
    public DuplicateResourceException(String resource, Object id) {
        super("Ya existe un " + resource + " con identificador '" + id + "'");
    }
}
