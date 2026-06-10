package com.mascotas.mascotas.dto;

import lombok.Data;
import java.time.LocalDate;
import com.fasterxml.jackson.annotation.JsonFormat;

@Data
public class UsuarioDTO {
 
    private Integer idUsuario;
    private String run; // Aquí va el RUT formateado (ej: 12345678-9)
    private String nombre;
    private String apellido1;
    private String apellido2;
    private String email;
    private String telefono;
    
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate fechaNacimiento;
    
    private String rol;
}
