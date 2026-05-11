package com.mascotas.mascotas.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MascotaCreateDTO {

    private String chipMascota;

    @NotBlank(message = "El nombre de la mascota es obligatorio.")
    private String nombreMascota;

    @NotBlank(message = "La especie de la mascota es obligatoria.")
    private String especie;

    @NotBlank(message = "La raza de la mascota es obligatoria.")
    private String raza;

    @NotBlank(message = "El sexo de la mascota es obligatorio.")
    private String sexo;

    @NotBlank(message = "El tamaño de la mascota es obligatorio.")
    private String tamaño;

    @NotBlank(message = "El color de la mascota es obligatorio.")
    private String color;

}
