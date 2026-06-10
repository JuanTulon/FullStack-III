package com.mascotas.mascotas.exception;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {//para cuando no se encuentra un dato en la bd
        super(message);
    }
}
