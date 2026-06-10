package com.mascotas.mascotas.controller;

import com.mascotas.mascotas.dto.UsuarioCreateDTO;
import com.mascotas.mascotas.dto.UsuarioDTO;
import com.mascotas.mascotas.security.JwtService;
import com.mascotas.mascotas.service.UsuarioService;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;
import com.mascotas.mascotas.security.JwtAuthenticationFilter;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

//@Disabled
@WebMvcTest(controllers = UsuarioController.class)
@AutoConfigureMockMvc(addFilters = false) // Apagamos la seguridad JWT solo para aislar el test del controlador
public class UsuarioControllerTest {
    
    @Autowired
    private MockMvc mockMvc; // Simula las peticiones HTTP (Postman)

    @MockBean
    private UsuarioService usuarioService;

    // Mockeamos las dependencias de seguridad para que el contexto levante sin errores
    @MockBean
    private JwtService jwtService;
    
    @MockBean
    private UserDetailsService userDetailsService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    private org.springframework.security.authentication.AuthenticationProvider authenticationProvider;

    @Test
    @DisplayName("registrarUsuario: Retorna 201 Created si el JSON es perfecto")
    void registrarUsuario_Exito() throws Exception {
        // En lugar de un DTO de Java, armamos el JSON tal como lo enviaría el Frontend
        String requestJson = """
                {
                    "run": "19876543-0",
                    "nombre": "Juan",
                    "apellido1": "Perez",
                    "email": "juan@test.com",
                    "password": "123456",
                    "telefono": "987654321",
                    "fechaNacimiento": "01-01-1990",
                    "rol": "USUARIO"
                }
                """;

        // Preparar lo que el Service va a responder
        UsuarioDTO response = new UsuarioDTO();
        response.setNombre("Juan");
        response.setEmail("juan@test.com");

        when(usuarioService.registrarUsuario(any(UsuarioCreateDTO.class))).thenReturn(response);

        // Ejecutar la petición HTTP POST simulada y verificar resultados
        mockMvc.perform(post("/api/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson)) // <-- Pasamos el String directamente
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombre").value("Juan"))
                .andExpect(jsonPath("$.email").value("juan@test.com"));
    }

    @Test
    @DisplayName("registrarUsuario: Retorna 400 Bad Request si faltan datos obligatorios")
    void registrarUsuario_FallaValidacion() throws Exception {
        String requestIncompleto = """
                {
                    "nombre": "Juan"
                }
                """;

        mockMvc.perform(post("/api/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestIncompleto))
                .andDo(print()) // <--- MAGIA AQUÍ: Esto imprimirá el error real en la consola
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("asignarAdmin: Retorna 200 OK si un ADMIN promueve a otro usuario")
    void asignarAdmin_Exito() throws Exception {
        // Simulamos un DTO de respuesta
        UsuarioDTO response = new UsuarioDTO();
        response.setRol("ADMIN");
        response.setEmail("nuevoadmin@test.com");

        // Mockeamos el servicio
        when(usuarioService.asignarRolAdmin(any(Integer.class))).thenReturn(response);

        // Ejecutamos la petición PUT
        mockMvc.perform(put("/api/usuarios/1/rol-admin")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rol").value("ADMIN"));
    }
}
