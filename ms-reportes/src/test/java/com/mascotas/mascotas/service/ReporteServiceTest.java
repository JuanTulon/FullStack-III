package com.mascotas.mascotas.service;

import com.mascotas.mascotas.dto.ReporteCreateDTO;
import com.mascotas.mascotas.dto.ReporteDTO;
import com.mascotas.mascotas.dto.ReporteUpdateDTO;
import com.mascotas.mascotas.exception.BusinessRuleException;
import com.mascotas.mascotas.model.Reporte;
import com.mascotas.mascotas.repository.ReporteRepository;
import com.mascotas.mascotas.security.CustomUserDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReporteServiceTest {
    
    @Mock
    private ReporteRepository reporteRepository;

    @InjectMocks
    private ReporteService reporteService;

    private Reporte reporteSimulado;
    private ReporteCreateDTO createDTO;

    @BeforeEach
    void setUp() {
        reporteSimulado = new Reporte();
        reporteSimulado.setIdReporte(100);
        reporteSimulado.setUsuarioId(1);
        reporteSimulado.setMascotaId(5);
        reporteSimulado.setTipo(Reporte.TipoReporte.PERDIDO);
        reporteSimulado.setEstadoReporte(Reporte.EstadoReporte.ACTIVO);
        reporteSimulado.setFechaReporte(LocalDateTime.now());

        createDTO = new ReporteCreateDTO();
        createDTO.setMascotaId(5);
        createDTO.setTipo("PERDIDO");
        createDTO.setEstado("ACTIVO");
        createDTO.setDescripcion("Se perdió ayer");
    }

    private void mockSecurityContext(int idUsuario) {
        Authentication authentication = mock(Authentication.class);
        CustomUserDetails userDetails = mock(CustomUserDetails.class);
        lenient().when(userDetails.getIdUsuario()).thenReturn(idUsuario);
        lenient().when(authentication.getPrincipal()).thenReturn(userDetails);
        
        SecurityContext securityContext = mock(SecurityContext.class);
        lenient().when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    @DisplayName("registrarReporte: Exito si la mascota NO tiene reporte activo")
    void registrarReporte_Exito() {
        mockSecurityContext(1);
        // Aquí simulamos que NO existe un reporte activo
        when(reporteRepository.existeReporteActivoPorMascotaId(eq(5), eq(Reporte.EstadoReporte.ACTIVO))).thenReturn(false);
        when(reporteRepository.save(any(Reporte.class))).thenReturn(reporteSimulado);

        ReporteDTO resultado = reporteService.registrarReporte(createDTO, "dueno@test.com");

        assertThat(resultado).isNotNull();
        assertThat(resultado.getEstado()).isEqualTo("ACTIVO");
    }

    @Test
    @DisplayName("registrarReporte: Falla si la mascota YA TIENE un reporte activo")
    void registrarReporte_FallaReporteExistente() {
        mockSecurityContext(1);
        // Aquí simulamos que SÍ existe un reporte activo, debe saltar la BusinessRuleException
        when(reporteRepository.existeReporteActivoPorMascotaId(eq(5), eq(Reporte.EstadoReporte.ACTIVO))).thenReturn(true);

        BusinessRuleException ex = assertThrows(BusinessRuleException.class, 
            () -> reporteService.registrarReporte(createDTO, "dueno@test.com"));

        assertThat(ex.getMessage()).isEqualTo("Esta mascota ya tiene un reporte ACTIVO en el sistema.");
        verify(reporteRepository, never()).save(any(Reporte.class));
    }

    @Test
    @DisplayName("actualizarReporte: Falla por Zero Trust (Intruso)")
    void actualizarReporte_FallaZeroTrust() {
        mockSecurityContext(2); // Hacker autenticado
        when(reporteRepository.findById(100)).thenReturn(Optional.of(reporteSimulado));
        ReporteUpdateDTO updateDTO = new ReporteUpdateDTO();

        BusinessRuleException ex = assertThrows(BusinessRuleException.class, 
            () -> reporteService.actualizarReporte(100, updateDTO, "hacker@test.com"));

        assertThat(ex.getMessage()).isEqualTo("No tienes permisos para modificar este reporte.");
    }
}
