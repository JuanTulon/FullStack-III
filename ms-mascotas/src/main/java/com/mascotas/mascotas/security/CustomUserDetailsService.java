package com.mascotas.mascotas.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Devuelve un usuario virtual para cumplir con la interfaz en este microservicio independiente
        return new CustomUserDetails(
                email,
                "",
                List.of(new SimpleGrantedAuthority("ROLE_USER")),
                null
        );
    }
}
