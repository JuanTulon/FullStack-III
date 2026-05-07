package com.mascotas.mascotas.repository;

import com.mascotas.mascotas.model.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.junit.jupiter.api.Disabled;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
@Disabled
@DataJpaTest
@org.springframework.test.context.ContextConfiguration(classes = com.mascotas.mascotas.MascotasApplication.class)
class UsuarioRepositoryTest {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private TestEntityManager entityManager; // Para guardar datos sin usar el mismo repo que probaremos

    private Usuario testUser;

    @BeforeEach
    void setUp() {
        // Configuramos un usuario de prueba para persistir antes de cada test.
        // No incluyo ID porque la JPA se encarga (strategy = GenerationType.IDENTITY)
        testUser = new Usuario();
        testUser.setNombre("Juan");
        testUser.setApellido1("Perez");
        testUser.setApellido2("Gomez");
        testUser.setRun("12345678-5");
        testUser.setEmail("juan@test.com");
        testUser.setPassword("12345");
        testUser.setTelefono("912345678");
        testUser.setRol(Usuario.Rol.USUARIO);

        // Guarda el usuario directamente en la base de datos en memoria (H2)
        entityManager.persistAndFlush(testUser);
    }

    @Test
    @DisplayName("findByEmail: Debería retornar usuario cuando el email existe")
    void findByEmail_RetornaUsuario() {
        Optional<Usuario> userOptional = usuarioRepository.findByEmail("juan@test.com");

        assertThat(userOptional).isPresent();
        assertThat(userOptional.get().getNombre()).isEqualTo("Juan");
    }

    @Test
    @DisplayName("findByEmail: Debería retornar vacío si el email no existe")
    void findByEmail_NoRetornaNada() {
        Optional<Usuario> userOptional = usuarioRepository.findByEmail("inexistente@test.com");

        assertThat(userOptional).isEmpty();
    }

    @Test
    @DisplayName("existsByRun: Debería ser true si el RUT ya está siendo usado")
    void existsByRun_RetornaTrueSiExiste() {
        boolean existe = usuarioRepository.existsByRun("12345678-5");
        assertThat(existe).isTrue();
    }

    @Test
    @DisplayName("buscarPorApellido1: Debería retornar lista de usuarios con mismo apellido materno")
    void buscarPorApellido1_RetornaListaMatch() {
        // Agregamos otro con mismo apellido1
        Usuario testUser2 = new Usuario();
        testUser2.setNombre("Pedro");
        testUser2.setApellido1("Perez");
        testUser2.setEmail("pedro@test.com");
        testUser2.setPassword("pwd");
        testUser2.setRun("19876543-0");
        testUser2.setTelefono("99887766");
        testUser2.setRol(Usuario.Rol.USUARIO);
        entityManager.persistAndFlush(testUser2);

        List<Usuario> resultados = usuarioRepository.buscarPorApellido1("Perez");

        assertThat(resultados).hasSize(2);
        assertThat(resultados.get(0).getNombre()).isIn("Juan", "Pedro");
    }
}
