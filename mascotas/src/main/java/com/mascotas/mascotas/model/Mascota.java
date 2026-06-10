package com.mascotas.mascotas.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Entity
@Table(name= "mascota")  
@Data  
@NoArgsConstructor  
@AllArgsConstructor  
@Schema(name = "Mascota", description = "Entidad que representa a una mascota del sistema.")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Mascota {

    public enum Especie {
        PERRO, GATO, OTRO
    }

    public enum Tamaño {
        PEQUEÑO, MEDIANO, GRANDE
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador único de la mascota.", example = "1")
    private Integer idMascota;


    @Column(unique = false, nullable = true, length = 30)
    @Schema(description = "chip de la mascota.", example = "123456789012345678")
    private String chipMascota;

    @Column(nullable=false,length = 90)  // Define las restricciones para la columna en la tabla.
    @Schema(description = "Nombre de la mascota", example = "Firulais")
    private String nombreMascota;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false, length = 50)  // Define las restricciones para la columna en la tabla.
    @Schema(description = "especie a la cual pertenece la mascota", example = "Perro")
    private Especie especie;

    @Column(nullable=false, length = 50)
    @Schema(description = "Raza de la mascota", example = "Labrador")
    private String raza;

    @Column(nullable=false, length = 50)
    @Schema(description = "Sexo de la mascota", example = "Macho")
    private String sexo;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false, length = 50)
    @Schema(description = "tamaño de la mascota", example = "Grande")
    private Tamaño tamaño;

    @Column(nullable=false, length = 50)
    @Schema(description = "color principal de la mascota", example = "Negro")
    private String color;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @OneToMany(mappedBy = "mascota", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore 
    private List<Reporte> reportes;
}