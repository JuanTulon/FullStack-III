package com.mascotas.mascotas.security;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    
    private final JwtAuthenticationFilter jwtAuthFilter;

    @Value("${frontend.url}")
    private String frontendUrl;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();   
        authProvider.setUserDetailsService(userDetailsService);     
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, AuthenticationProvider authProvider) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            // Solo se declara la gestión de sesión una vez
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                //1. Rutas 100% Públicas
                .requestMatchers("/api/auth/**").permitAll() // Login
                .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/usuarios").permitAll() // Registro
                
                //2. Rutas de Lectura Públicas (Ver mapa y mascotas)
                .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/reporte/**").permitAll()
                .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/mascota/**").permitAll()
                .requestMatchers(org.springframework.http.HttpMethod.GET, "/uploads/**").permitAll()

                
                //3. Documentación Swagger
                .requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**").permitAll()
                
                //4. Rutas Restringidas a ADMIN
                .requestMatchers(org.springframework.http.HttpMethod.DELETE, "/api/usuarios/**").hasRole("ADMIN")
                .requestMatchers(org.springframework.http.HttpMethod.PUT, "/api/usuarios/*/rol-admin").hasRole("ADMIN")

                //5. Todo lo demás es Privado (Requiere Token)
                .anyRequest().authenticated()
            )
            .authenticationProvider(authProvider)
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        // 1. Configuración general para la API
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of(frontendUrl)); // Inyectamos el host dinámicamente
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        configuration.setAllowCredentials(false);

        // 2. Configuración específica para las fotos (públicas)
        CorsConfiguration uploadsConfiguration = new CorsConfiguration();
        uploadsConfiguration.setAllowedOrigins(List.of("*")); // Permitimos que cualquier origen lea las fotos públicamente
        uploadsConfiguration.setAllowedMethods(Arrays.asList("GET", "OPTIONS"));
        uploadsConfiguration.setAllowedHeaders(List.of("*"));
        uploadsConfiguration.setAllowCredentials(false);

        // 3. Registrar ambas configuraciones
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/uploads/**", uploadsConfiguration); // Regla específica para fotos
        source.registerCorsConfiguration("/**", configuration); // Regla general
        
        return source;
    }


}
