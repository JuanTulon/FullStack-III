package com.mascotas.mascotas.config;

import com.mascotas.mascotas.model.Usuario;
import com.mascotas.mascotas.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;

@Configuration
public class DataInitializer {
    
    @Bean
    CommandLineRunner initDatabase(UsuarioRepository repository, PasswordEncoder passwordEncoder) {
        return args -> {
            // 1. CREAR USUARIO ADMINISTRADOR (Si no existe)
            if (repository.findByEmail("admin@mascotas.cl").isEmpty()) {
                Usuario admin = new Usuario();
                admin.setRun("11111111-1"); // RUN de prueba
                admin.setNombre("Admin");
                admin.setApellido1("Sistema");
                admin.setEmail("admin@mascotas.cl");
                admin.setTelefono("911111111");
                admin.setFechaNacimiento(LocalDate.of(1990, 1, 1));
                admin.setPassword(passwordEncoder.encode("admin123")); // Contraseña encriptada
                admin.setRol(Usuario.Rol.ADMIN); // Asignamos rol ADMIN directamente
                
                repository.save(admin);
                System.out.println("✅ Usuario ADMIN creado por defecto (admin@mascotas.cl / admin123)");
            }

            // 2. CREAR USUARIO NORMAL (Si no existe)
            if (repository.findByEmail("user@mascotas.cl").isEmpty()) {
                Usuario user = new Usuario();
                user.setRun("22222222-2");
                user.setNombre("Usuario");
                user.setApellido1("Prueba");
                user.setEmail("user@mascotas.cl");
                user.setTelefono("922222222");
                user.setFechaNacimiento(LocalDate.of(1995, 5, 10));
                user.setPassword(passwordEncoder.encode("user123"));
                user.setRol(Usuario.Rol.USUARIO); // Rol normal
                
                repository.save(user);
                System.out.println("✅ Usuario NORMAL creado por defecto (user@mascotas.cl / user123)");
            }
        };
    }
}
