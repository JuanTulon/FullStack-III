package com.mascotas.mascotas.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mascotas.mascotas.dto.ReporteCreateDTO;
import com.mascotas.mascotas.dto.ReporteDTO;
import com.mascotas.mascotas.service.ReporteService;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//@Disabled
@WebMvcTest(controllers = ReporteController.class)
@AutoConfigureMockMvc(addFilters = false)
public class ReporteControllerTest {
    
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ReporteService reporteService;

    @MockBean
    private com.mascotas.mascotas.security.JwtService jwtService;
    
    @MockBean
    private com.mascotas.mascotas.security.CustomUserDetailsService customUserDetailsService;
    
    @MockBean
    private com.mascotas.mascotas.security.JwtAuthenticationFilter jwtAuthenticationFilter;
    
    @MockBean
    private org.springframework.security.authentication.AuthenticationProvider authenticationProvider;

    @Test
    @DisplayName("registrarReporte: Retorna 201 Created con JSON válido")
    void registrarReporte_Exito() throws Exception {
        // 1. Creamos un usuario falso (Principal) para evitar el NullPointerException (Error 500)
        java.security.Principal mockPrincipal = org.mockito.Mockito.mock(java.security.Principal.class);
        when(mockPrincipal.getName()).thenReturn("juan@test.com");

        // 2. JSON más completo (Agregué latitud, longitud y usuarioId por si tu DTO los pide)
        String requestJson = """
                {
                    "mascotaId": 1,
                    "tipo": "PERDIDO",
                    "estado": "ACTIVO",
                    "descripcion": "Se perdió en el parque",
                    "latitud": -33.456,
                    "longitud": -70.654,
                    "usuarioId": 1
                }
                """;

        ReporteDTO response = new ReporteDTO();
        response.setIdReporte(100);
        response.setTipo("PERDIDO");
        response.setEstado("ACTIVO");

        // Simulamos la respuesta del servicio (usando any() para evitar problemas de tipos)
        when(reporteService.registrarReporte(org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.anyString()))
                .thenReturn(response);

        // 3. Ejecutamos la petición con el JSON, el Principal y los Rayos X
        mockMvc.perform(post("/api/reporte")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson)
                .principal(mockPrincipal)) // <--- Inyectamos el usuario falso
                .andDo(org.springframework.test.web.servlet.result.MockMvcResultHandlers.print()) // <--- RAYOS X ACTIVADOS
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("registrarReporte: Retorna 400 Bad Request si faltan datos")
    void registrarReporte_FallaValidacion() throws Exception {
        ReporteCreateDTO request = new ReporteCreateDTO();
        // Enviando DTO vacío para que Spring lo bloquee

        mockMvc.perform(post("/api/reporte")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
