package com.mascotas.mascotas.dto;

import lombok.Data;

@Data
public class MascotaDTO {
    
    private Integer idMascota;
    private String chipMascota;
    private String nombreMascota;
    private String especie;
    private String raza;
    private String sexo;
    private String tamaño;
    private String color;
    private Integer usuarioId;
}
