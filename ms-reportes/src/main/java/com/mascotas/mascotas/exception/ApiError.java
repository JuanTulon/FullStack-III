package com.mascotas.mascotas.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
public class ApiError {
    
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime timestamp;
    
    private Integer status; // Código HTTP (ej: 400, 404, 409)
    private String error;   // Nombre del error (ej: "Not Found", "Conflict")
    private String message; // Mensaje legible (ej: "El usuario con ID 5 no existe")
    private String path;    // La ruta que falló (ej: "/api/v1/usuarios/5")
    
    // Este mapa nos servirá en el Paso 4 para mandar los errores de los DTOs
    // Ej: {"email": "El formato es inválido", "rut": "No puede estar vacío"}
    private Map<String, String> fieldErrors;
}
