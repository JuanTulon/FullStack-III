package com.mascotas.mascotas.service;

import com.mascotas.mascotas.dto.MascotaCreateDTO;
import com.mascotas.mascotas.dto.MascotaDTO;
import com.mascotas.mascotas.dto.MascotaUpdateDTO;
import com.mascotas.mascotas.exception.BusinessRuleException;
import com.mascotas.mascotas.model.Mascota;
import com.mascotas.mascotas.repository.MascotaRepository;
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

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MascotaServiceTest {
    @Mock
    private MascotaRepository mascotaRepository;

    @InjectMocks
    private MascotaService mascotaService;

    private Mascota mascotaSimulada;
    private MascotaCreateDTO createDTO;

    @BeforeEach
    void setUp() {
        mascotaSimulada = new Mascota();
        mascotaSimulada.setIdMascota(10);
        mascotaSimulada.setNombreMascota("Firulais");
        mascotaSimulada.setEspecie(Mascota.Especie.PERRO);
        mascotaSimulada.setTamaño(Mascota.Tamaño.MEDIANO);
        mascotaSimulada.setUsuarioId(1);

        createDTO = new MascotaCreateDTO();
        createDTO.setNombreMascota("Firulais");
        createDTO.setEspecie("PERRO");
        createDTO.setTamaño("MEDIANO");
    }

    private void mockSecurityContext(int idUsuario) {
        Authentication authentication = mock(Authentication.class);
        CustomUserDetails userDetails = mock(CustomUserDetails.class);
        when(userDetails.getIdUsuario()).thenReturn(idUsuario);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    @DisplayName("registrarMascota: Éxito asocia mascota al usuario logueado")
    void registrarMascota_Exito() {
        mockSecurityContext(1);
        when(mascotaRepository.save(any(Mascota.class))).thenReturn(mascotaSimulada);

        MascotaDTO resultado = mascotaService.registrarMascota(createDTO, "dueno@test.com");

        assertThat(resultado).isNotNull();
        assertThat(resultado.getEspecie()).isEqualTo("PERRO");
    }

    @Test
    @DisplayName("actualizarMascota: Falla por Zero Trust (Intruso)")
    void actualizarMascota_FallaPorIntruso() {
        mockSecurityContext(2);
        when(mascotaRepository.findById(10)).thenReturn(Optional.of(mascotaSimulada));

        MascotaUpdateDTO updateDTO = new MascotaUpdateDTO();
        updateDTO.setNombre("Nuevo Nombre");

        BusinessRuleException ex = assertThrows(BusinessRuleException.class, 
            () -> mascotaService.actualizarMascota(10, updateDTO, "hacker@test.com"));

        assertThat(ex.getMessage()).isEqualTo("No tienes permiso para editar esta mascota.");
    }

    @Test
    @DisplayName("eliminarMascota: Falla por Zero Trust (Intruso)")
    void eliminarMascota_FallaPorIntruso() {
        mockSecurityContext(2);
        when(mascotaRepository.findById(10)).thenReturn(Optional.of(mascotaSimulada));

        BusinessRuleException ex = assertThrows(BusinessRuleException.class, 
            () -> mascotaService.eliminarMascota(10, "hacker@test.com"));

        assertThat(ex.getMessage()).isEqualTo("No tienes permiso para eliminar esta mascota.");
    }
}
