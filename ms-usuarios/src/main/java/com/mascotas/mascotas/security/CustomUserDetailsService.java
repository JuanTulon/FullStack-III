package com.mascotas.mascotas.security;

import com.mascotas.mascotas.model.Usuario;
import com.mascotas.mascotas.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    
    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con email: " + email));

        return new CustomUserDetails(
                usuario.getEmail(),
                usuario.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_" + usuario.getRol().name())),
                usuario.getIdUsuario()
        );
    }
}
