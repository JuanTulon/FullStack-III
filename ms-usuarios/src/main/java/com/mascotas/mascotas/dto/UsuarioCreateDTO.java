package com.mascotas.mascotas.dto;

import java.time.LocalDate;
import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UsuarioCreateDTO {
    
    @NotBlank(message = "El RUN completo es obligatorio (ej: 12345678-9).")
    private String run;

    @NotBlank(message = "El nombre es obligatorio.")
    private String nombre;

    @NotBlank(message = "El primer apellido es obligatorio.")
    private String apellido1;

    private String apellido2;

    @Email(message = "El email debe ser válido.")
    @NotBlank(message = "El email es obligatorio.")
    private String email;

    @NotBlank(message = "El teléfono es obligatorio.")
    private String telefono;

    @NotNull(message = "La fecha de nacimiento es obligatoria.")
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate fechaNacimiento;

    @NotBlank(message = "La contraseña es obligatoria.")
    private String password;

    @NotBlank(message = "El rol es obligatorio.")
    private String rol;
}
