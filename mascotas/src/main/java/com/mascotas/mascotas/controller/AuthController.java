package com.mascotas.mascotas.controller;

import com.mascotas.mascotas.dto.AuthResponse;
import com.mascotas.mascotas.dto.LoginRequest;
import com.mascotas.mascotas.model.Usuario;
import com.mascotas.mascotas.repository.UsuarioRepository;
import com.mascotas.mascotas.security.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private JwtService jwtService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        //1. autenticar con spring security(verifica email y pass encriptada)
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        // 2. Si pasa, buscamos el usuario para generar el token
        Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // 3. Generar el Token JWT
        String jwtToken = jwtService.generateToken(new org.springframework.security.core.userdetails.User(
                usuario.getEmail(), 
                usuario.getPassword(), 
                java.util.List.of(new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_" + usuario.getRol().name()))
        ));

        return ResponseEntity.ok(new AuthResponse(jwtToken, usuario.getNombre(), String.valueOf(usuario.getIdUsuario()), usuario.getRol().name()));
    }
}
