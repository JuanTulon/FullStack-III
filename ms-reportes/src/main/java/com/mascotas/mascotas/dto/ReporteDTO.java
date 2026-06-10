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
    
    private Double latitud;
    private Double longitud;

    private Integer usuarioId;
    private Integer mascotaId;
    private List<String> urlsFotos;
}
