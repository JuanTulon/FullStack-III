package com.mascotas.mascotas.model;

import java.time.LocalDate;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name= "usuario")  
@Data  
@NoArgsConstructor  
@AllArgsConstructor  
@Schema(name = "Usuario", description = "Entidad que representa a un usuario del sistema.")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Usuario {

    public enum Rol {
        ADMIN, USUARIO
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador único del usuario.", example = "1")
    private Integer idUsuario;

    @Column(unique=true, length = 13, nullable=false)  // Define las restricciones para la columna en la tabla.
    @Schema(description = "RUN del usuario con dígito verificador y guion", example = "12345678-9")
    private String run;

    @Column(nullable=false, length = 30)  // Define las restricciones para la columna en la tabla.
    @Schema(description = "Fecha de nacimiento del usuario con el formato dd-MM-yyyy", example = "15-08-1990")
    @JsonFormat(pattern = "dd-MM-yyyy")  // Especifica el formato de fecha para la serialización/deserialización JSON.
    private LocalDate fechaNacimiento;
    
    @Column(nullable=false, length = 100)  // Define las restricciones para la columna en la tabla.
    @Schema(description = "Nombre del usuario", example = "Juan")
    private String nombre; 

    @Column(nullable=false, length = 100)  // Define las restricciones para la columna en la tabla.
    @Schema(description = "Primer apellido del usuario", example = "Pérez")
    private String apellido1;
    
    @Column(nullable=true, length = 100)
    @Schema(description = "Segundo apellido del usuario", example = "González")
    private String apellido2; 

    @Column(nullable=false, length = 100)  // Esta columna puede ser nula.
    @Schema(description = "Correo electrónico del usuario", example = "juan.perez@gmail.com")
    private String email;

    @Column(nullable=false, length = 15)  // Esta columna no puede ser nula.
    @Schema(description = "Teléfono de contacto del usuario", example = "+56912345678")
    private String telefono;
    
    @Column(nullable=false, length = 100)  // Define las restricciones para la columna en la tabla.
    @Schema(description = "contraseña del usuario", example = "password123")
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false, length = 20)  // Define las restricciones para la columna en la tabla.
    @Schema(description = "Rol del usuario en el sistema", example = "USUARIO")
    private Rol rol;

}
