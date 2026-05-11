package com.mascotas.mascotas.controller;

import com.mascotas.mascotas.dto.MascotaDTO;
import com.mascotas.mascotas.dto.MascotaUpdateDTO;
import com.mascotas.mascotas.dto.MascotaCreateDTO;
import com.mascotas.mascotas.service.MascotaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.security.Principal;
import java.util.List;

//@CrossOrigin("ip-address")
@Tag(name = "mascota", description = "Gestión de mascotas del sistema")
@RestController
@RequestMapping("/api/mascota")
public class MascotaController {

    @Autowired
    private MascotaService mascotaService;

    //LISTAR MASCOTAS
    @Operation(summary = "Listar todas las mascotas", responses = {
        @ApiResponse(responseCode = "200", description = "Lista encontrada",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = MascotaDTO.class),
                examples = @ExampleObject(value = """
                    [
                        {
                            "idMascota": 1,
                            "chipMascota": "123456789012345",
                            "nombreMascota": "Bobby",
                            "especie": "PERRO",
                            "raza": "Poodle",
                            "sexo": "Macho",
                            "tamaño": "PEQUEÑO",
                            "color": "Blanco"
                        }
                    ]
                """)
            )
        ),
        @ApiResponse(responseCode = "204", description = "No hay mascotas", content = @Content)
    })
    @GetMapping
    public ResponseEntity<List<MascotaDTO>> listar() {
        List<MascotaDTO> mascotas = mascotaService.listarmascotas();
        return mascotas.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(mascotas);
    }

    //CREAR mascota
    @Operation(summary = "Registrar nueva mascota", description = "Crea una nueva mascota.", responses = {
        @ApiResponse(responseCode = "201", description = "Mascota creada",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = MascotaDTO.class),
                examples = @ExampleObject(name = "Mascota Nueva", value = """
                    {
                        "chipMascota": "123456789012345",
                        "nombreMascota": "Bobby",
                        "especie": "PERRO",
                        "raza": "Poodle",
                        "sexo": "Macho",
                        "tamaño": "PEQUEÑO",
                        "color": "Blanco",
                        "usuarioId": 1
                    }
                """)
            )
        ),
        @ApiResponse(responseCode = "400", description = "Datos inválidos ", content = @Content)
    })
    @PostMapping
    public ResponseEntity<MascotaDTO> guardar(@Valid @RequestBody MascotaCreateDTO request, Principal principal) {
        String emailUsuario = principal.getName();
        // El service ahora debe recibir el email para asociar la mascota al dueño real
        MascotaDTO nueva = mascotaService.registrarMascota(request, emailUsuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(nueva);
    }

    //BUSCAR POR ID
    @Operation(summary = "Buscar por ID", responses = {
        @ApiResponse(responseCode = "200", description = "Encontrado", content = @Content(schema = @Schema(implementation = MascotaDTO.class))),
        @ApiResponse(responseCode = "404", description = "No encontrado", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<MascotaDTO> buscar(@PathVariable Integer id) {
        return mascotaService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    //BUSCAR POR ESPECIE
    @Operation(summary = "Buscar por especie", description = "Busca mascotas por su especie (Perro, Gato, Otro).", responses = {
        @ApiResponse(responseCode = "200", description = "Encontrado", content = @Content(schema = @Schema(implementation = MascotaDTO.class))),
        @ApiResponse(responseCode = "404", description = "No encontrado", content = @Content)
    })
    @GetMapping("/especie/{especie}")
    public ResponseEntity<List<MascotaDTO>> buscarPorEspecie(@PathVariable String especie) {
        List<MascotaDTO> mascotas = mascotaService.buscarPorEspecie(especie);
        return mascotas.isEmpty() ? ResponseEntity.notFound().build() : ResponseEntity.ok(mascotas);
    }

    //BUSCAR POR TAMAÑO
    @Operation(summary = "Buscar por tamaño", description = "Busca mascotas por su tamaño (Pequeño, Mediano, Grande).", responses = {
        @ApiResponse(responseCode = "200", description = "Encontrado", content = @Content(schema = @Schema(implementation = MascotaDTO.class))),
        @ApiResponse(responseCode = "404", description = "No encontrado", content = @Content)
    })
    @GetMapping("/tamano/{tamano}")
    public ResponseEntity<List<MascotaDTO>> buscarPorTamanio(@PathVariable String tamano) {
        List<MascotaDTO> mascotas = mascotaService.buscarPorTamaño(tamano);
        return mascotas.isEmpty() ? ResponseEntity.notFound().build() : ResponseEntity.ok(mascotas);
    }

    //BUSCAR POR CHIP
    @Operation(summary = "Buscar por chip", description = "Busca una mascota por su número de chip.", responses = {
        @ApiResponse(responseCode = "200", description = "Encontrado", content = @Content(schema = @Schema(implementation = MascotaDTO.class))),
        @ApiResponse(responseCode = "404", description = "No encontrado", content = @Content)
    })
    @GetMapping("/chip/{chip}")
    public ResponseEntity<MascotaDTO> buscarPorChip(@PathVariable String chip) {
        return mascotaService.buscarPorChip(chip)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    //ACTUALIZAR MASCOTA (PUT)
    @Operation(summary = "Actualizar datos", description = "Actualiza los datos básicos de la mascota. No actualiza el chip ni el dueño.", responses = {
        @ApiResponse(responseCode = "200", description = "Actualizado correctamente",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = MascotaDTO.class),
                examples = @ExampleObject(name = "Datos a Actualizar", value = """
                    {
                        "nombre": "Bobby",
                        "raza": "Poodle Toy",
                        "sexo": "Macho",
                        "color": "Blanco",
                        "especie": "Perro",
                        "tamaño": "PEQUEÑO"
                    }
                """)
            )
        ),
        @ApiResponse(responseCode = "404", description = "mascota no encontrada", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<MascotaDTO> actualizar(@PathVariable Integer id, @Valid @RequestBody MascotaUpdateDTO mascotaUpdate, Principal principal) {
        String emailUsuario = principal.getName();
        return ResponseEntity.ok(mascotaService.actualizarMascota(id, mascotaUpdate, emailUsuario));
    }

    // ELIMINAR MASCOTA
    @Operation(summary = "Eliminar mascota por id", responses = {
        @ApiResponse(responseCode = "204", description = "Mascota eliminada", content = @Content),
        @ApiResponse(responseCode = "404", description = "Mascota no encontrada", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@Valid @PathVariable Integer id, Principal principal) {
        String emailUsuario = principal.getName();
        mascotaService.eliminarMascota(id, emailUsuario);
        return ResponseEntity.noContent().build();
    }
}