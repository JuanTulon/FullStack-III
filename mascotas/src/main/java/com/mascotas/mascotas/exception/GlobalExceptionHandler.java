package com.mascotas.mascotas.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    
    /* 1. Atrapa los errores 404 (No Encontrado)
    // @ExceptionHandler le dice a Spring que este método debe ejecutarse automáticamente cuando se lance 
     una excepción del tipo ResourceNotFoundException en cualquier parte del código*/
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> handleResourceNotFoundException(ResourceNotFoundException ex, WebRequest request) {
        // Inicializamos la construcción del objeto estructurado ApiError utilizando el patrón Builder de Lombok
        ApiError apiError = ApiError.builder()
                // Asignamos la fecha y hora exacta en la que se generó este error utilizando LocalDateTime
                .timestamp(LocalDateTime.now())
                // Asignamos el código numérico del estado HTTP, el .value() de NOT_FOUND extrae el número 404
                .status(HttpStatus.NOT_FOUND.value())
                // Asignamos el texto estándar del estado HTTP, el .getReasonPhrase() para NOT_FOUND nos dará el texto "Not Found"
                .error(HttpStatus.NOT_FOUND.getReasonPhrase())
                // Extraemos el mensaje personalizado desde la excepción (ex) que la originó para mostrarle al cliente qué falló ("Usuario no encontrado")
                .message(ex.getMessage()) // Aquí va el "Usuario no encontrado con ID: 5"
                // Obtenemos la ruta de la petición que falló. getDescription(false) trae texto como "uri=/api/v1/user/1". 
                // Luego con replace limpiamos ese prefijo "uri=" dejando solamente el path limpio (ej: "/api/v1/user/1")
                .path(request.getDescription(false).replace("uri=", ""))
                // Ensambla y crea de una vez el objeto ApiError con todas las propiedades que indicamos arriba
                .build();
        
        // Retornamos un ResponseEntity que envuelve nuestra respuesta de error personalizada (apiError) y le incrusta el estatus HTTP 404 que le llegará finalmente al cliente
        return new ResponseEntity<>(apiError, HttpStatus.NOT_FOUND);
    }

    // 2. Atrapa los errores de Reglas de Negocio (Ej: RUT duplicado, Mascota con reporte)
    // Cuando el código lance un BusinessRuleException (ej: "El correo ya existe"), Spring redirigirá el flujo a este método
    @ExceptionHandler(BusinessRuleException.class)
    public ResponseEntity<ApiError> handleBusinessRuleException(BusinessRuleException ex, WebRequest request) {
        // Iniciamos la creación de nuestra respuesta de error (ApiError)
        ApiError apiError = ApiError.builder()
                // Asignamos el momento temporal exacto del error
                .timestamp(LocalDateTime.now())
                // .value() saca el número 409, que corresponde a "Conflict" e indica conflicto en el estado actual (ej de registro duplicado)
                .status(HttpStatus.CONFLICT.value()) // 409 Conflict es ideal para reglas de negocio
                // Texto representativo HTTP, mostrará "Conflict"
                .error(HttpStatus.CONFLICT.getReasonPhrase())
                // Traslada el mensaje de validación de negocio al cliente web (Ej: "La mascota ya está adoptada")
                .message(ex.getMessage())
                // Extrae y limpia la ruta o endpoint desde donde se ejecutó la petición causante del conflicto
                .path(request.getDescription(false).replace("uri=", ""))
                // Concretiza y construye el objeto de error
                .build();
        
        // Devuelve el objeto ApiError serializado en JSON junto con la cabecera HTTP 409 CONFLICT hacia el cliente web/Postman
        return new ResponseEntity<>(apiError, HttpStatus.CONFLICT);
    }

    // 3. El "Atrapa-Todo" (Por si se nos escapa algún error crítico del servidor, como que se caiga la BD)
    // Exception.class es la clase 'padre' de todas las excepciones en Java. Si ocurre un error no previsto (ej: NullPointerException, error de base de datos) caerá aquí.
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGlobalException(Exception ex, WebRequest request) {
        // Se construye el reporte de error general que le daremos al usuario
        ApiError apiError = ApiError.builder()
                // Asigna la fecha y hora del desastre inesperado
                .timestamp(LocalDateTime.now())
                // Establece el código "500" (.value()), que significa "Error en el servidor", pues el fallo es nuestra responsabilidad, no del cliente
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                // Entrega el texto "Internal Server Error"
                .error(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                // A diferencia de los otros métodos, aquí escondemos el mensaje real (ex.getMessage()) por seguridad (para no filtrar detalles de base de datos a un atacante)
                // y ponemos un mensaje genérico tranquilizador
                .message("ERROR REAL: " + ex.getClass().getSimpleName() + " - " + ex.getMessage())
                // Opcional para debug: .message(ex.getMessage()) 
                // Toma la ruta solicitada donde de manera fortuita se rompió la aplicación y la limpia
                .path(request.getDescription(false).replace("uri=", ""))
                // Termina el ensamblaje del mensaje
                .build();
        
        // Empaqueta todo el error 500 y se lo envía al cliente de manera estructurada gracias a nuestro molde ApiError
        return new ResponseEntity<>(apiError, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // 4. Atrapa los errores de @Valid en los DTOs
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidationExceptions(MethodArgumentNotValidException ex, WebRequest request) {
        
        // Declaramos un 'Map' (un diccionario clave-valor) llamado 'errores' para guardar dinámicamente 
        // el nombre del campo que falló y su respectivo mensaje de error.
        Map<String, String> errores = new HashMap<>();
        
        // Iteramos sobre la lista de errores que Spring capturó en la excepción 'ex' (proveniente de un @Valid que no pasó la prueba en el Controller).
        // 'ex.getBindingResult().getFieldErrors()' extrae la lista de todos los campos específicos que violaron las reglas de validación.
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            // Por cada error encontrado, lo guardamos en nuestro diccionario:
            // 'error.getField()' actúa como la Llave (ej: "email" o "password") 
            // 'error.getDefaultMessage()' actúa como el Valor (ej: "Debe proporcionar un email válido")
            errores.put(error.getField(), error.getDefaultMessage());
        }

        ApiError apiError = ApiError.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value()) // 400 Bad Request
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message("Error en la validación de los datos enviados.")
                .path(request.getDescription(false).replace("uri=", ""))
                .fieldErrors(errores) // Inyectamos el mapa de errores
                .build();
        
        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }
}
