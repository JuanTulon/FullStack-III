package com.mascotas.mascotas.exception;

public class BusinessRuleException extends RuntimeException {
    public BusinessRuleException(String message) { //para cuando se incumple una regla de negocio
        super(message);
    }   
}
