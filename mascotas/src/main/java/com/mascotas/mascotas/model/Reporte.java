package com.mascotas.mascotas.model;

import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name= "reporte")  
@Data  
@NoArgsConstructor  
@AllArgsConstructor  
@Schema(name = "Reporte", description = "Entidad que representa a un reporte del sistema.")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Reporte {

    public enum TipoReporte {
        ENCONTRADO, PERDIDO, AVISTADA
    }

    public enum EstadoReporte {
        ACTIVO, RESUELTO, CANCELADO
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador único del reporte.", example = "1")
    private Integer idReporte;

    @Column(name = "url_foto", nullable = true, length = 255)
    @Schema(description = "URL de la foto del reporte", example = "http://example.com/foto.jpg")
    private String urlFoto;
    //Le pusimos nullable = true porque si por alguna razón falla la subida, al menos el reporte no rompe la base de datos, aunque en el controlador exijamos que la envíen.

    @Column(nullable = false, length = 50)
    @Schema(description = "Tipo de reporte", example = "ENCONTRADO")
    private TipoReporte tipo;

    @Column(nullable = false, length = 100)
    @Schema(description = "Fecha del reporte con el formato DD-MM-YYYY", example = "15-08-2024")
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDateTime fechaReporte;

    @Column(nullable = false, length = 500)
    @Schema(description = "Descripción del reporte", example = "Se encontró un perro labrador cerca del parque central.")
    private String descripcion;

    @Column(nullable = false, length = 50)
    @Schema(description = "estado del reporte", example = "Activo")
    private EstadoReporte estadoReporte;

    @Column(nullable = false, length = 100)
    @Schema(description = "Latitud del lugar del reporte", example = "-33.4489")
    private Double latitud;

    @Column(nullable = false, length = 100)
    @Schema(description = "Longitud del lugar del reporte", example = "-70.6693")
    private Double longitud;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    @JsonIgnoreProperties({"reportes", "password", "hibernateLazyInitializer", "handler"}) 
    @JsonBackReference("usuario-reportes")
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_mascota", nullable = false)
    @JsonIgnoreProperties({"reportes", "hibernateLazyInitializer", "handler"})
    private Mascota mascota;
}
