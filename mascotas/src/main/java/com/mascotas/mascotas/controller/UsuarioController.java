package com.mascotas.mascotas.controller;

import com.mascotas.mascotas.dto.UsuarioDTO;
import com.mascotas.mascotas.dto.UsuarioUpdateDTO;
import com.mascotas.mascotas.dto.UsuarioCreateDTO;
import com.mascotas.mascotas.model.Usuario;
import com.mascotas.mascotas.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
import java.util.List;

//@CrossOrigin("ip-address")
@Tag(name = "Usuarios", description = "Gestión de usuarios del sistema")
@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {
    
    @Autowired
    private UsuarioService usuarioService;

    // --- LISTAR USUARIOS ---
    @Operation(summary = "Listar todos los usuarios", responses = {
        @ApiResponse(responseCode = "200", description = "Lista encontrada",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = UsuarioDTO.class),
                examples = @ExampleObject(value = """
                    [
                        {
                            "idUsuario": 1,
                            "run": "11111111-1",
                            "nombre": "Juan",
                            "apellido1": "Pérez",
                            "apellido2": "González",
                            "email": "juan.perez@example.com",
                            "telefono": "987654321",
                            "fechaNacimiento": "15-05-1990",
                            "rol": "USUARIO"
                        }
                    ]
                """)
            )
        ),
        @ApiResponse(responseCode = "204", description = "No hay usuarios", content = @Content)
    })
    @GetMapping
    public ResponseEntity<List<UsuarioDTO>> listar() {
        List<UsuarioDTO> usuarios = usuarioService.listarUsuarios();
        return usuarios.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(usuarios);
    }

    // --- CREAR USUARIO (POST) ---
    @Operation(summary = "Registrar nuevo usuario", description = "Crea un usuario con rol por defecto.", responses = {
        @ApiResponse(responseCode = "201", description = "Usuario creado",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = Usuario.class),
                examples = @ExampleObject(name = "Usuario Nuevo", value = """
                    {
                        "run": "12345678-5",
                        "nombre": "Maria",
                        "apellido1": "López",
                        "apellido2": "Torres",
                        "fechaNacimiento": "20-10-1995",
                        "email": "maria.lopez@example.com",
                        "telefono": "912345678",
                        "password": "PasswordSegura123",
                        "rol": "USUARIO"
                    }
                """)
            )
        ),
        @ApiResponse(responseCode = "400", description = "Datos inválidos (RUT erróneo o email duplicado)", content = @Content)
    })
    @PostMapping
    public ResponseEntity<UsuarioDTO> guardar(@Valid @RequestBody UsuarioCreateDTO request) {
        UsuarioDTO nuevo = usuarioService.registrarUsuario(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
    }

    // --- BUSCAR POR ID ---
    @Operation(summary = "Buscar por ID", responses = {
        @ApiResponse(responseCode = "200", description = "Encontrado", content = @Content(schema = @Schema(implementation = UsuarioDTO.class))),
        @ApiResponse(responseCode = "404", description = "No encontrado", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioDTO> buscar(@PathVariable Integer id) {
        return usuarioService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    //BUSCAR POR RUT
    @Operation(summary = "Buscar por RUT completo", parameters = {
        @Parameter(name = "rut", description = "RUT con guion (ej: 12345678-5)", required = true, example = "12345678-5")
    })
    @GetMapping("/rut/{rut}")
    public ResponseEntity<List<UsuarioDTO>> buscarPorRut(@Valid @PathVariable String rut) {
        List<UsuarioDTO> usuarios = usuarioService.buscarPorRut(rut);
        return usuarios.isEmpty() ? ResponseEntity.notFound().build() : ResponseEntity.ok(usuarios);
    }

    //ACTUALIZAR USUARIO (PUT)
    @Operation(summary = "Actualizar perfil", description = "Actualiza solo nombre, apellidos, teléfono, rol y email. No actualiza RUT ni contraseña.", responses = {
        @ApiResponse(responseCode = "200", description = "Actualizado correctamente",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = UsuarioDTO.class),
                examples = @ExampleObject(name = "Datos a Actualizar", value = """
                    {
                        "nombre": "Maria Alejandra",
                        "apellido1": "López",
                        "apellido2": "Torres",
                        "email": "maria.nueva@example.com",
                        "telefono": "555666777"
                    }
                """)
            )
        ),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado", content = @Content)
    })
    @PutMapping("/perfil")//ruta protegida, solo el usuario logueado puede actualizar su perfil
    public ResponseEntity<UsuarioDTO> actualizar(java.security.Principal principal, @Valid @RequestBody UsuarioUpdateDTO usuarioUpdate) {
        String email = principal.getName();
        return ResponseEntity.ok(usuarioService.actualizarPerfil(email, usuarioUpdate));
    }

    // ELIMINAR USUARIO solo admin
    @Operation(summary = "Eliminar usuario por id (Requiere rol ADMIN)", responses = {
        @ApiResponse(responseCode = "204", description = "Usuario eliminado", content = @Content),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        usuarioService.eliminarUsuario(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Promover usuario a ADMIN (Requiere rol ADMIN)", responses = {
        @ApiResponse(responseCode = "200", description = "Usuario promovido a ADMIN",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = UsuarioDTO.class),
                examples = @ExampleObject(name = "Usuario Promovido", value = """
                    {
                        "idUsuario": 2,
                        "run": "87654321-9",
                        "nombre": "Carlos",
                        "apellido1": "Sánchez",
                        "apellido2": "Díaz",
                        "email": "carlos.sanchez@example.com",
                        "telefono": "998877665",
                        "fechaNacimiento": "15-05-1990",
                        "rol": "ADMIN"
                    }
                """)
            )
        ),
        @ApiResponse(responseCode = "403", description = "Acceso denegado (Requiere token de ADMIN)", content = @Content),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado", content = @Content)
    })
    @PutMapping("/{id}/rol-admin")
    public ResponseEntity<UsuarioDTO> asignarAdmin(@PathVariable Integer id) {
        UsuarioDTO actualizado = usuarioService.asignarRolAdmin(id);
        return ResponseEntity.ok(actualizado);
    }
}
