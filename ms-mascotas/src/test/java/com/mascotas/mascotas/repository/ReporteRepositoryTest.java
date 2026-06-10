package com.mascotas.mascotas.repository;

import com.mascotas.mascotas.model.Mascota;
import com.mascotas.mascotas.model.Reporte;
import com.mascotas.mascotas.model.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.junit.jupiter.api.Disabled;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

//@Disabled
@DataJpaTest
@org.springframework.test.context.ContextConfiguration(classes = com.mascotas.mascotas.MascotasApplication.class)
class ReporteRepositoryTest {

    @Autowired
    private ReporteRepository reporteRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Usuario theOwner;
    private Mascota theMascota;

    @BeforeEach
    void setUp() {
        theOwner = new Usuario();
        theOwner.setNombre("CreadorReporte");
        theOwner.setApellido1("Apellido");
        theOwner.setRun("2-7");
        theOwner.setEmail("reporte@test.com");
        theOwner.setPassword("pass");
        theOwner.setTelefono("9112233");
        theOwner.setRol(Usuario.Rol.USUARIO);
        theOwner.setFechaNacimiento(java.time.LocalDate.of(1992, 8, 25));
        entityManager.persistAndFlush(theOwner);

        theMascota = new Mascota();
        theMascota.setNombreMascota("Mascota Perdida");
        theMascota.setChipMascota("CHIP99");
        theMascota.setEspecie(Mascota.Especie.GATO);
        theMascota.setTamaño(Mascota.Tamaño.MEDIANO);
        theMascota.setRaza("Angora");
        theMascota.setSexo("Hembra");
        theMascota.setColor("Blanco");
        theMascota.setUsuario(theOwner);
        entityManager.persistAndFlush(theMascota);

        Reporte reporte = new Reporte();
        reporte.setDescripcion("Perdido cerca de plaza");
        reporte.setLatitud(-33.4489);
        reporte.setLongitud(-70.6693);
        reporte.setTipo(Reporte.TipoReporte.PERDIDO);
        reporte.setEstadoReporte(Reporte.EstadoReporte.ACTIVO);
        reporte.setFechaReporte(java.time.LocalDateTime.now());
        reporte.setUsuario(theOwner);
        reporte.setMascota(theMascota);
        entityManager.persistAndFlush(reporte);
    }

    @Test
    @DisplayName("findByTipo: Debería encontrar todos los reportes por tipo")
    void findByTipo_RetornaReportes() {
        List<Reporte> perdidos = reporteRepository.findByTipo(Reporte.TipoReporte.PERDIDO);

        assertThat(perdidos).hasSize(1);
        assertThat(perdidos.get(0).getTipo()).isEqualTo(Reporte.TipoReporte.PERDIDO);
    }

    @Test
    @DisplayName("findByTipoAndEstadoReporte: Debería encontrar reporte por tipo y estado")
    void findByTipoAndEstadoReporte_RetornaMatchPerfecto() {
        List<Reporte> match = reporteRepository.findByTipoAndEstadoReporte(Reporte.TipoReporte.PERDIDO, Reporte.EstadoReporte.ACTIVO);

        assertThat(match).isNotEmpty();
        assertThat(match.get(0).getMascota().getChipMascota()).isEqualTo("CHIP99");
    }

    @Test
    @DisplayName("existeReporteActivoPorMascota: Debería retornar true si hay un reporte activo")
    void existeReporteActivoPorMascota_RetornaTrue() {
        boolean existe = reporteRepository.existeReporteActivoPorMascota(theMascota, Reporte.EstadoReporte.ACTIVO);
        assertThat(existe).isTrue();
    }
}
