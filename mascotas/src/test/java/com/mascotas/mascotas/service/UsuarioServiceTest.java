package com.mascotas.mascotas.service;

import com.mascotas.mascotas.dto.UsuarioCreateDTO;
import com.mascotas.mascotas.dto.UsuarioDTO;
import com.mascotas.mascotas.dto.UsuarioUpdateDTO;
import com.mascotas.mascotas.exception.BusinessRuleException;
import com.mascotas.mascotas.model.Usuario;
import com.mascotas.mascotas.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
public class UsuarioServiceTest {
    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioService usuarioService;

    private UsuarioCreateDTO createDTO;
    private Usuario usuarioSimulado;

    @BeforeEach
    void setUp() {
        createDTO = new UsuarioCreateDTO();
        // Usamos un RUT válido real para que pase el RutUtils.validarRut()
        createDTO.setRun("19876543-0"); 
        createDTO.setNombre("Juan");
        createDTO.setApellido1("Perez");
        createDTO.setEmail("juan@test.com");
        createDTO.setPassword("123456");
        createDTO.setFechaNacimiento(LocalDate.of(1990, 1, 1));

        usuarioSimulado = new Usuario();
        usuarioSimulado.setIdUsuario(1);
        usuarioSimulado.setRun("19876543-0");
        usuarioSimulado.setNombre("Juan");
        usuarioSimulado.setApellido1("Perez");
        usuarioSimulado.setEmail("juan@test.com");
        usuarioSimulado.setPassword("encriptado");
        usuarioSimulado.setRol(Usuario.Rol.USUARIO);
    }

    @Test
    @DisplayName("registrarUsuario: Éxito si todo es válido")
    void registrarUsuario_Exito() {
        when(usuarioRepository.buscarPorRut("19876543-0")).thenReturn(Collections.emptyList());
        when(usuarioRepository.findByEmail(createDTO.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encriptado");
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioSimulado);

        UsuarioDTO resultado = usuarioService.registrarUsuario(createDTO);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getEmail()).isEqualTo("juan@test.com");
    }

    @Test
    @DisplayName("registrarUsuario: Falla si RUT es inválido")
    void registrarUsuario_FallaRutInvalido() {
        createDTO.setRun("12345678-4"); // DV Incorrecto
        assertThrows(BusinessRuleException.class, () -> usuarioService.registrarUsuario(createDTO));
    }

    @Test
    @DisplayName("actualizarPerfil: Éxito si se actualizan datos sin colisión de email")
    void actualizarPerfil_Exito() {
        UsuarioUpdateDTO updateDTO = new UsuarioUpdateDTO();
        updateDTO.setNombre("Juan Modificado");
        updateDTO.setEmail("juan@test.com"); // Mantiene el mismo email
        updateDTO.setRol("USUARIO");

        when(usuarioRepository.findByEmail("juan@test.com")).thenReturn(Optional.of(usuarioSimulado));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioSimulado);

        UsuarioDTO resultado = usuarioService.actualizarPerfil("juan@test.com", updateDTO);
        assertThat(resultado).isNotNull();
    }

    @Test
    @DisplayName("buscarPorEmail: Retorna DTO si existe")
    void buscarPorEmail_Exito() {
        when(usuarioRepository.findByEmail("juan@test.com")).thenReturn(Optional.of(usuarioSimulado));
        Optional<UsuarioDTO> resultado = usuarioService.buscarPorEmail("juan@test.com");
        assertThat(resultado).isPresent();
        assertThat(resultado.get().getNombre()).isEqualTo("Juan");
    }
}
