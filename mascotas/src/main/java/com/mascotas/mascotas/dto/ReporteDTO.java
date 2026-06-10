package com.mascotas.mascotas.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ReporteDTO {
    
    private Integer idReporte;
    private String tipo; 
    private String estado;
    private LocalDateTime fecha;
    private String descripcion;
    
    // Solo enviamos las coordenadas para el mapa
    private Double latitud;
    private Double longitud;

    // NO enviamos el objeto Usuario completo, solo lo necesario para contactar
    private String nombreContacto;
    private String telefonoContacto;

    // Datos de la mascota simplificados
    private String nombreMascota;
    private String razaMascota;
    private List<String> urlsFotos;
}
