package com.mascotas.mascotas.repository;

import com.mascotas.mascotas.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;//import que ofrece los métodos CRUD
import org.springframework.data.jpa.repository.Query;//permite escribir consultas personalizadas
import org.springframework.data.repository.query.Param;//permite escribir consultas personalizadas
import org.springframework.stereotype.Repository;//marca esta clase interfaz como repositorio de spring
import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    // Método mágico para Login (busca por email)
    Optional<Usuario> findByEmail(String email);

    // Validar si existe por RUT (útil para registro)
    boolean existsByRun(String run);

    // Búsquedas personalizadas usando JPQL (Más seguro que nativeQuery)
    @Query("SELECT u FROM Usuario u WHERE u.apellido1 = :apellido1")
    List<Usuario> buscarPorApellido1(@Param("apellido1") String apellido1);

    @Query("select u from Usuario u where u.run = :run")
    List<Usuario> buscarPorRut(@Param("run") String run);
}