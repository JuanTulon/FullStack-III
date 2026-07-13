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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.mock.web.MockMultipartFile;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;

import static org.mockito.Mockito.when;
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
    private com.mascotas.mascotas.service.S3Service s3Service;

    @MockBean
    private com.mascotas.mascotas.security.JwtService jwtService;
    
    @MockBean
    private com.mascotas.mascotas.security.CustomUserDetailsService customUserDetailsService;
    
    @MockBean
    private com.mascotas.mascotas.security.JwtAuthenticationFilter jwtAuthenticationFilter;
    
    @MockBean
    private org.springframework.security.authentication.AuthenticationProvider authenticationProvider;

    @Test
    @DisplayName("registrarReporte: Retorna 201 Created con JSON válido y Foto")
    void registrarReporte_Exito() throws Exception {
        java.security.Principal mockPrincipal = org.mockito.Mockito.mock(java.security.Principal.class);
        when(mockPrincipal.getName()).thenReturn("juan@test.com");

        String requestJson = """
                {
                    "mascotaId": 1,
                    "tipo": "PERDIDO",
                    "estado": "ACTIVO",
                    "descripcion": "Se perdió en el parque",
                    "latitud": -33.456,
                    "longitud": -70.654
                }
                """;

        // Creamos una foto falsa en memoria
        MockMultipartFile fotoMock = new MockMultipartFile(
                "foto", "perrito.jpg", "image/jpeg", "bytes de imagen falsa".getBytes()
        );

        // Convertimos nuestro JSON en un "archivo" multipart para poder enviarlo junto a la foto
        MockMultipartFile reporteMock = new MockMultipartFile(
                "reporte", "", "application/json", requestJson.getBytes()
        );

        ReporteDTO response = new ReporteDTO();
        response.setIdReporte(100);
        response.setTipo("PERDIDO");

        when(s3Service.uploadFile(org.mockito.ArgumentMatchers.any()))
                .thenReturn("https://my-bucket.s3.amazonaws.com/perrito.jpg");

        when(reporteService.registrarReporte(org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.anyString()))
                .thenReturn(response);

        mockMvc.perform(multipart("/api/reporte")
                .file(fotoMock)
                .file(reporteMock)
                .principal(mockPrincipal))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("registrarReporte: Retorna 400 Bad Request si faltan datos")
    void registrarReporte_FallaValidacion() throws Exception {
        ReporteCreateDTO request = new ReporteCreateDTO();
        // Enviando DTO vacío para que Spring lo bloquee

        mockMvc.perform(multipart("/api/reporte")
                .file(new MockMultipartFile("reporte", "", "application/json", objectMapper.writeValueAsString(request).getBytes())))
                .andExpect(status().isBadRequest());
    }
}
