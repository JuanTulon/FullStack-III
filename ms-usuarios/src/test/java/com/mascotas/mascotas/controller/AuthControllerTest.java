package com.mascotas.mascotas.controller;

import com.mascotas.mascotas.security.JwtAuthenticationFilter;
import com.mascotas.mascotas.security.JwtService;
import com.mascotas.mascotas.repository.UsuarioRepository;
import com.mascotas.mascotas.model.Usuario;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private UsuarioRepository usuarioRepository;

    @MockBean
    private JwtService jwtService;
    @MockBean
    private UserDetailsService userDetailsService;
    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;
    @MockBean
    private AuthenticationProvider authenticationProvider;

    @Test
    @DisplayName("login: Retorna 200 OK y Token si credenciales son correctas")
    void login_Exito() throws Exception {
        String requestJson = """
                {
                    "email": "juan@test.com",
                    "password": "123456"
                }
                """;

        // 1. Simulamos que la autenticación (Spring Security) pasa sin errores
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(null);

        com.mascotas.mascotas.model.Usuario mockUser = new com.mascotas.mascotas.model.Usuario();
        mockUser.setEmail("juan@test.com");
        mockUser.setPassword("123456"); 
        
        mockUser.setRol(com.mascotas.mascotas.model.Usuario.Rol.USUARIO); 
        
        when(usuarioRepository.findByEmail("juan@test.com"))
                .thenReturn(java.util.Optional.of(mockUser));
        
        when(jwtService.generateToken(any(), any()))
                .thenReturn("token.falso.123");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("token.falso.123"));
    }
}
