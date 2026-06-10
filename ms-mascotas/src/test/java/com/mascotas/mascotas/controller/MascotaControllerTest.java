package com.mascotas.mascotas.controller;

import com.mascotas.mascotas.dto.MascotaCreateDTO;
import com.mascotas.mascotas.dto.MascotaDTO;
import com.mascotas.mascotas.security.JwtAuthenticationFilter;
import com.mascotas.mascotas.security.JwtService;
import com.mascotas.mascotas.service.MascotaService;
import java.security.Principal;

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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.security.authentication.AuthenticationProvider;

//@Disabled
@WebMvcTest(controllers = MascotaController.class)
@AutoConfigureMockMvc(addFilters = false) // Apagamos seguridad JWT temporalmente para aislar el test
class MascotaControllerTest {
@Autowired
    private MockMvc mockMvc;

    @MockBean
    private MascotaService mascotaService;

    // --- BLOQUE DE SEGURIDAD EXACTO ---
    @MockBean
    private JwtService jwtService;
    @MockBean
    private UserDetailsService userDetailsService;
    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;
    @MockBean
    private AuthenticationProvider authenticationProvider;

    @Test
    @DisplayName("registrarMascota: Retorna 201 Created con JSON válido")
    void registrarMascota_Exito() throws Exception {
        // 1. Creamos un usuario falso (Principal) usando Mockito para evitar el NullPointerException
        Principal mockPrincipal = org.mockito.Mockito.mock(Principal.class);
        when(mockPrincipal.getName()).thenReturn("dueño@test.com");

        // 2. Armamos el JSON completo asegurando que el chip esté en camelCase
        String requestJson = """
                {
                    "chipMascota": "123456789012345",
                    "nombreMascota": "Firulais",
                    "especie": "PERRO",
                    "tamaño": "MEDIANO",
                    "raza": "Mestizo",
                    "sexo": "Macho",
                    "color": "Café",
                    "usuarioId": 1
                }
                """;

        // 3. Mockeamos lo que va a responder el servicio
        MascotaDTO response = new MascotaDTO();
        response.setNombreMascota("Firulais");
        response.setEspecie("PERRO");

        when(mascotaService.registrarMascota(any(MascotaCreateDTO.class), anyString()))
                .thenReturn(response);

        // 4. Ejecutamos la petición HTTP enviando el JSON y adjuntando nuestro usuario falso
        mockMvc.perform(post("/api/mascota")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson)
                .principal(mockPrincipal)) // Inyectamos el Principal
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombreMascota").value("Firulais"));
    }

    @Test
    @DisplayName("registrarMascota: Retorna 400 Bad Request si faltan datos")
    void registrarMascota_FallaValidacion() throws Exception {
        String requestIncompleto = """
                {
                    "especie": "PERRO"
                }
                """; // Falta el nombre, debería fallar la validación

        mockMvc.perform(post("/api/mascota")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestIncompleto))
                .andExpect(status().isBadRequest());
    }
}