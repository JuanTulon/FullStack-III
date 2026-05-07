package com.mascotas.mascotas.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReporteCreateDTO {
    
    @NotBlank(message = "El tipo de reporte es obligatorio.")
    private String tipo;

    @NotBlank(message = "el estado del reporte es obligatorio.")
    private String estado;

    @NotBlank(message = "La descripción del reporte es obligatoria.")
    private String descripcion;

    @NotNull(message = "La latitud es obligatoria.")
    private Double latitud;

    @NotNull(message = "La longitud es obligatoria.")
    private Double longitud;
    
    @NotNull(message = "El id de la mascota es obligatorio.")
    private Integer mascotaId;

}
