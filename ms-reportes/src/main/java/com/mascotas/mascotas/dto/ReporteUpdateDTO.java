package com.mascotas.mascotas.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReporteUpdateDTO {
    
    @NotBlank(message = "El tipo de reporte es obligatorio.")
    private String tipo; 

    @NotBlank(message = "El estado del reporte es obligatorio.")
    private String estado;

    @NotBlank(message = "La descripción del reporte es obligatoria.")
    private String descripcion;

    @NotNull(message = "La latitud es obligatoria.")
    private Double latitud;

    @NotNull(message = "La longitud es obligatoria.")
    private Double longitud;
}
