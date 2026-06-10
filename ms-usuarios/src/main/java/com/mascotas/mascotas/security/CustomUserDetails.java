package com.mascotas.mascotas.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import java.util.Collection;

public class CustomUserDetails extends User {
    private final Integer idUsuario;

    public CustomUserDetails(String username, String password, Collection<? extends GrantedAuthority> authorities, Integer idUsuario) {
        super(username, password, authorities);
        this.idUsuario = idUsuario;
    }

    public Integer getIdUsuario() {
        return idUsuario;
    }
}
