package com.mascotas.mascotas.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Email;

@Data
public class UsuarioUpdateDTO {
    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;
    
    @NotBlank(message = "El primer apellido es obligatorio")
    private String apellido1;
    
    private String apellido2;
    
    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "Formato de correo inválido")
    private String email;
    
    @NotBlank(message = "El teléfono es obligatorio")
    private String telefono;
    
    private String rol;
}
