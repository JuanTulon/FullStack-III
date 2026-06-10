package com.mascotas.mascotas.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;

@Data
public class MascotaUpdateDTO {
    
    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;
    
    @NotBlank(message = "La raza es obligatoria")
    private String raza;
    
    @NotBlank(message = "El sexo es obligatorio")
    private String sexo;
    
    @NotBlank(message = "El color es obligatorio")
    private String color;
    
    @NotBlank(message = "La especie es obligatoria")
    private String especie;
    
    @NotBlank(message = "El tamaño es obligatorio")
    private String tamaño;
}
