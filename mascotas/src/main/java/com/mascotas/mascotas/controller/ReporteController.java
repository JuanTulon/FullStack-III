package com.mascotas.mascotas.controller;

import com.mascotas.mascotas.dto.ReporteCreateDTO;
import com.mascotas.mascotas.dto.ReporteDTO;
import com.mascotas.mascotas.dto.ReporteUpdateDTO;
import com.mascotas.mascotas.service.ReporteService;
import java.security.Principal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.multipart.MultipartFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import org.springframework.http.MediaType;

import java.util.List;

//@CrossOrigin("ip-address")
@Tag(name = "reporte", description = "Gestión de reportes del sistema")
@RestController
@RequestMapping("/api/reporte")
public class ReporteController {
    
    @Autowired
    private ReporteService reporteService;

    //LISTAR reportes
    @Operation(summary = "Listar todos los reportes", responses = {
        @ApiResponse(responseCode = "200", description = "Lista encontrada",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = ReporteDTO.class),
                examples = @ExampleObject(value = """
                    [
                        {
                            "id_reporte": 1,
                            "tipo": "PERDIDO",
                            "estado": "ABIERTO",
                            "fecha": "2023-09-20T14:30:00",
                            "descripcion": "Reporte de mascota perdida",
                            "latitud": -33.4489,
                            "longitud": -70.6693,
                            "nombreContacto": "Juan Pérez",
                            "telefonoContacto": 912345678,
                            "nombreMascota": "Bobby",
                            "razaMascota": "Poodle"
                        }
                    ]
                """)
            )
        ),
        @ApiResponse(responseCode = "204", description = "No hay reportes", content = @Content)
    })
    @GetMapping
    public ResponseEntity<List<ReporteDTO>> listar() {
        List<ReporteDTO> reportes = reporteService.listarReportes();
        return reportes.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(reportes);
    }

    //CREAR reporte
    @Operation(summary = "Registrar nuevo reporte", description = "Crea un reporte de mascota perdida.", responses = {
        @ApiResponse(responseCode = "201", description = "Reporte creado",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = ReporteDTO.class),
                examples = @ExampleObject(name = "Reporte Nuevo", value = """
                    {
                        "tipo": "PERDIDO",
                        "estado": "ABIERTO",
                        "descripcion": "Reporte de mascota perdida",
                        "latitud": -33.4489,
                        "longitud": -70.6693,
                        "mascotaId": 1,
                        "usuarioId": 1
                    }
                """)
            )
        ),
        @ApiResponse(responseCode = "400", description = "Datos inválidos ", content = @Content)
    })
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ReporteDTO> guardar(
            java.security.Principal principal,
            @Valid @RequestPart("reporte") ReporteCreateDTO request,
            @RequestPart("foto") MultipartFile foto) {

        // 1. Validar que la foto venga sí o sí
        if (foto == null || foto.isEmpty()) {
            return ResponseEntity.badRequest().build(); // O lanza tu BusinessRuleException
        }

        // 2. Guardar la foto físicamente
        try {
            Path directorioImagenes = Paths.get("uploads");
            if (!Files.exists(directorioImagenes)) {
                Files.createDirectories(directorioImagenes);
            }
            // Generar nombre único
            String nombreArchivo = UUID.randomUUID().toString() + "_" + foto.getOriginalFilename();
            Path rutaCompleta = directorioImagenes.resolve(nombreArchivo);
            
            // Copiar archivo a la carpeta
            Files.copy(foto.getInputStream(), rutaCompleta);

            // 3. Inyectar el nombre de la foto en el DTO antes de pasarlo al Service
            request.setUrlFoto(nombreArchivo);

        } catch (Exception e) {
            throw new RuntimeException("Error al guardar la foto en el servidor", e);
        }

        // 4. Llamar al servicio normal (asumiendo que tu servicio extrae el email del principal)
        String emailUsuario = (principal != null) ? principal.getName() : "test@test.com";
        ReporteDTO nuevo = reporteService.registrarReporte(request, emailUsuario);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
    }

    //BUSCAR POR ID
    @Operation(summary = "Buscar por ID", responses = {
        @ApiResponse(responseCode = "200", description = "Encontrado", content = @Content(schema = @Schema(implementation = ReporteDTO.class))),
        @ApiResponse(responseCode = "404", description = "No encontrado", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<ReporteDTO> buscar(@PathVariable Integer id) {
        return reporteService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    //BUSCAR POR TIPO
    @Operation(summary = "Buscar por tipo de reporte", description = "Busca reportes por tipo (ENCONTRADO, PERDIDO, AVISTADA).", responses = {
        @ApiResponse(responseCode = "200", description = "Lista encontrada",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = ReporteDTO.class),
                examples = @ExampleObject(value = """
                    [
                        {
                            "id_reporte": 1,
                            "tipo": "PERDIDO",
                            "estado": "ABIERTO",
                            "fecha": "2023-09-20T14:30:00",
                            "descripcion": "Reporte de mascota perdida",
                            "latitud": -33.4489,
                            "longitud": -70.6693,
                            "nombreContacto": "Juan Pérez",
                            "telefonoContacto": 912345678,
                            "nombreMascota": "Bobby",
                            "razaMascota": "Poodle"
                        }
                    ]
                """)
            )
        ),
        @ApiResponse(responseCode = "204", description = "No hay reportes", content = @Content)
    })
    @GetMapping("/tipo/{tipo}")
    public ResponseEntity<List<ReporteDTO>> buscarPorTipo(@Valid @PathVariable String tipo) {
        List<ReporteDTO> reportes = reporteService.buscarPorTipo(tipo);
        return reportes.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(reportes);
    }

    //BUSCAR POR TIPO Y ESTADO
    @Operation(summary = "Buscar por tipo y estado de reporte", description = "Busca reportes por tipo (ENCONTRADO, PERDIDO, AVISTADA) y estado (ABIERTO, CERRADO).", responses = {
        @ApiResponse(responseCode = "200", description = "Lista encontrada",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = ReporteDTO.class),
                examples = @ExampleObject(value = """
                    [
                        {
                            "id_reporte": 1,
                            "tipo": "PERDIDO",
                            "estado": "ABIERTO",
                            "fecha": "2023-09-20T14:30:00",
                            "descripcion": "Reporte de mascota perdida",
                            "latitud": -33.4489,
                            "longitud": -70.6693,
                            "nombreContacto": "Juan Pérez",
                            "telefonoContacto": 912345678,
                            "nombreMascota": "Bobby",
                            "razaMascota": "Poodle"
                        }
                    ]
                """)
            )
        ),
        @ApiResponse(responseCode = "204", description = "No hay reportes", content = @Content)
    })
    @GetMapping("/tipo/{tipo}/estado/{estado}")
    public ResponseEntity<List<ReporteDTO>> buscarPorTipoYEstado(@Valid @PathVariable String tipo, @Valid @PathVariable String estado) {
        List<ReporteDTO> reportes = reporteService.buscarTipoYEstadoReporte(tipo, estado);
        return reportes.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(reportes);
    }

    // buscarPorEspecieYTipo
    @Operation(summary = "Buscar por especie y tipo de reporte", description = "Busca reportes por especie (ej: perro, gato) y tipo (ENCONTRADO, PERDIDO, AVISTADA).", responses = {
        @ApiResponse(responseCode = "200", description = "Lista encontrada",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = ReporteDTO.class),
                examples = @ExampleObject(value = """
                    [
                        {
                            "id_reporte": 1,
                            "tipo": "PERDIDO",
                            "estado": "ABIERTO",
                            "fecha": "2023-09-20T14:30:00",
                            "descripcion": "Reporte de mascota perdida",
                            "latitud": -33.4489,
                            "longitud": -70.6693,
                            "nombreContacto": "Juan Pérez",
                            "telefonoContacto": 912345678,
                            "nombreMascota": "Bobby",
                            "razaMascota": "Poodle"
                        }
                    ]
                """)
            )
        ),
        @ApiResponse(responseCode = "204", description = "No hay reportes", content = @Content)
    })
    @GetMapping("/especie/{especie}/tipo/{tipo}")
    public ResponseEntity<List<ReporteDTO>> buscarPorEspecieYTipo(@Valid @PathVariable String especie, @Valid @PathVariable String tipo) {
        List<ReporteDTO> reportes = reporteService.buscarPorEspecieYTipo(especie, tipo);
        return reportes.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(reportes);
    }

    //ACTUALIZAR reporte (PUT)
    @Operation(summary = "Actualizar datos", description = "Actualiza los datos de un reporte existente.", responses = {
        @ApiResponse(responseCode = "200", description = "Actualizado correctamente",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = ReporteDTO.class),
                examples = @ExampleObject(name = "Datos a Actualizar", value = """
                    {
                        "tipo": "PERDIDO",
                        "estado": "CERRADO",
                        "descripcion": "Reporte actualizado de mascota perdida",
                        "latitud": -33.4489,
                        "longitud": -70.6693
                    }
                """)
            )
        ),
        @ApiResponse(responseCode = "404", description = "Reporte no encontrado", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<ReporteDTO> actualizar(@PathVariable Integer id, @Valid @RequestBody ReporteUpdateDTO reporteUpdate, Principal principal) {
        String emailUsuario = principal.getName();
        return ResponseEntity.ok(reporteService.actualizarReporte(id, reporteUpdate, emailUsuario));        
    }

    // ELIMINAR REPORTE
    @Operation(summary = "Eliminar reporte por id", responses = {
        @ApiResponse(responseCode = "204", description = "Reporte eliminado", content = @Content),
        @ApiResponse(responseCode = "404", description = "Reporte no encontrado", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@Valid @PathVariable Integer id, Principal principal) {
        String emailUsuario = principal.getName();
        reporteService.eliminarReporte(id, emailUsuario);
        return ResponseEntity.noContent().build();
    }
}
