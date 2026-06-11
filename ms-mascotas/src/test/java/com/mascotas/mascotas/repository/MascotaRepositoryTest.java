package com.mascotas.mascotas.repository;

import com.mascotas.mascotas.model.Mascota;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@org.springframework.test.context.ContextConfiguration(classes = com.mascotas.mascotas.MascotasApplication.class)
class MascotaRepositoryTest {

    @Autowired
    private MascotaRepository mascotaRepository;

    @Autowired
    private TestEntityManager entityManager;

    @BeforeEach
    void setUp() {
        // Mascota de prueba
        Mascota mascota = new Mascota();
        mascota.setNombreMascota("Firulais");
        mascota.setChipMascota("123123");
        mascota.setEspecie(Mascota.Especie.PERRO);
        mascota.setTamaño(Mascota.Tamaño.MEDIANO);
        mascota.setRaza("Quiltro");
        mascota.setSexo("Macho");
        mascota.setColor("Cafe");
        mascota.setUsuarioId(1);
        entityManager.persistAndFlush(mascota);
    }

    @Test
    @DisplayName("findByEspecie: Encuentra mascotas de la especie correcta")
    void findByEspecie_EncuentraMascota() {
        List<Mascota> perros = mascotaRepository.findByEspecie(Mascota.Especie.PERRO);
        
        assertThat(perros).hasSize(1);
        assertThat(perros.get(0).getNombreMascota()).isEqualTo("Firulais");
    }

    @Test
    @DisplayName("findByTamaño: Encuentra las mascotas de tamaño especificado")
    void findByTamaño_EncuentraMascotas() {
        List<Mascota> medianos = mascotaRepository.findByTamaño(Mascota.Tamaño.MEDIANO);

        assertThat(medianos).isNotEmpty();
        assertThat(medianos.get(0).getTamaño()).isEqualTo(Mascota.Tamaño.MEDIANO);
    }

    @Test
    @DisplayName("findByChipMascota: Encuentra la mascota mediante su chip")
    void findByChipMascota_EncuentraElChip() {
        Optional<Mascota> encontrada = mascotaRepository.findByChipMascota("123123");

        assertThat(encontrada).isPresent();
        assertThat(encontrada.get().getChipMascota()).isEqualTo("123123");
    }
}
