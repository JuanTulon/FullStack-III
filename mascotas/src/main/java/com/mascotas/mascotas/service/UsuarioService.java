package com.mascotas.mascotas.service;

import org.springframework.security.crypto.password.PasswordEncoder;

import com.mascotas.mascotas.dto.UsuarioCreateDTO;
import com.mascotas.mascotas.dto.UsuarioDTO;
import com.mascotas.mascotas.dto.UsuarioUpdateDTO;
import com.mascotas.mascotas.exception.BusinessRuleException;
import com.mascotas.mascotas.exception.ResourceNotFoundException;
import com.mascotas.mascotas.model.Usuario;
import com.mascotas.mascotas.repository.UsuarioRepository;
import com.mascotas.mascotas.util.RutUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // --- LISTAR RETORNANDO DTOs ---
    public List<UsuarioDTO> listarUsuarios() {
        return usuarioRepository.findAll()
                .stream()
                .map(this::convertirADto) // Protegemos la información
                .toList();
    }

    // --- LISTAR Y BUSCAR ---

    public Optional<UsuarioDTO> buscarPorId(Integer id) {
        return usuarioRepository.findById(id).map(this::convertirADto);
    }

    public Optional<UsuarioDTO> buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email).map(this::convertirADto);
    }

    public List<UsuarioDTO> buscarPorRut(String rutCompleto) {
        if (!RutUtils.validarRut(rutCompleto)) {
            throw new BusinessRuleException("El RUT ingresado no es válido: " + rutCompleto);
        }
        
        return usuarioRepository.buscarPorRut(rutCompleto.toUpperCase())
                .stream().map(this::convertirADto).toList();
    }

    // CREACIÓN (REGISTRO)
    @Transactional
    public UsuarioDTO registrarUsuario(UsuarioCreateDTO request) {

        // 1. Validación de RUT completo
        if (!RutUtils.validarRut(request.getRun())) {
            throw new BusinessRuleException("El RUN ingresado no es válido.");
        }

        // 2. Comprobar que no exista el RUN ni el Email
        if (!usuarioRepository.buscarPorRut(request.getRun().toUpperCase()).isEmpty()) {
            throw new BusinessRuleException("El RUT ya está registrado en el sistema.");
        }
        
        if (usuarioRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new BusinessRuleException("El correo ya está en uso.");
        }

        // 3. Crear nueva entidad
        Usuario usuario = new Usuario();
        usuario.setRun(request.getRun().toUpperCase());
        usuario.setNombre(request.getNombre());
        usuario.setApellido1(request.getApellido1());
        usuario.setApellido2(request.getApellido2());
        usuario.setEmail(request.getEmail());
        usuario.setTelefono(request.getTelefono());
        usuario.setFechaNacimiento(request.getFechaNacimiento());
        usuario.setRol(Usuario.Rol.USUARIO); // Rol por defecto al registrarse

        // 4. Encriptar contraseña y guardar
        usuario.setPassword(passwordEncoder.encode(request.getPassword()));

        return convertirADto(usuarioRepository.save(usuario));
    }

    // --- ACTUALIZACIÓN ---
    @Transactional
    public UsuarioDTO actualizarPerfil(String emailActual, UsuarioUpdateDTO request) {
        Usuario usuario = usuarioRepository.findByEmail(emailActual)
            .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con correo: " + emailActual));

        // Validar si cambia el email y si el nuevo ya está ocupado por otro
        if (!usuario.getEmail().equals(request.getEmail()) &&
            usuarioRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new BusinessRuleException("El nuevo correo ya está en uso.");
        }

        // Actualizamos solo lo que el DTO permite
        usuario.setNombre(request.getNombre());
        usuario.setApellido1(request.getApellido1());
        usuario.setApellido2(request.getApellido2());
        usuario.setEmail(request.getEmail());
        usuario.setTelefono(request.getTelefono());
        if (request.getRol() != null) {
             usuario.setRol(Usuario.Rol.valueOf(request.getRol().toUpperCase()));
        }
        
        
        // El email solo se cambia si es distinto y no existe otro igual
        if (!usuario.getEmail().equals(request.getEmail()) && 
            usuarioRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new BusinessRuleException("El nuevo correo ya está en uso.");
        }
        usuario.setEmail(request.getEmail());

        return convertirADto(usuarioRepository.save(usuario));
    }

    // --- ELIMINAR ---

    @Transactional
    public void eliminarUsuario(Integer id) {
        if (!usuarioRepository.existsById(id)) {
            throw new ResourceNotFoundException("Usuario no encontrado con ID: " + id);
        }
        usuarioRepository.deleteById(id);
    }
    
    // MÉTODO AUXILIAR PARA LA SEGURIDAD
    private UsuarioDTO convertirADto(Usuario usuario) {
        UsuarioDTO dto = new UsuarioDTO();
        dto.setIdUsuario(usuario.getIdUsuario());
        dto.setNombre(usuario.getNombre());
        dto.setApellido1(usuario.getApellido1());
        dto.setApellido2(usuario.getApellido2());
        if (usuario.getRol() != null) {
            dto.setRol(usuario.getRol().name());
        }
        dto.setEmail(usuario.getEmail());
        dto.setTelefono(usuario.getTelefono());
        dto.setRun(usuario.getRun());
        dto.setFechaNacimiento(usuario.getFechaNacimiento());
        return dto;
    }
}
