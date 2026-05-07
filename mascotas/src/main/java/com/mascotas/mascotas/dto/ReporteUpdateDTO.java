package com.mascotas.mascotas.dto;

import lombok.Data;

@Data
public class ReporteUpdateDTO {
    private String tipo; 
    private String estado;
    private String descripcion;
    private Double latitud;
    private Double longitud;
}
