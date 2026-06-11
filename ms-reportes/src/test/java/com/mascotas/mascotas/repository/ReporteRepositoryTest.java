package com.mascotas.mascotas.repository;

import com.mascotas.mascotas.model.Reporte;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@org.springframework.test.context.ContextConfiguration(classes = com.mascotas.mascotas.MascotasApplication.class)
class ReporteRepositoryTest {

    @Autowired
    private ReporteRepository reporteRepository;

    @Autowired
    private TestEntityManager entityManager;

    @BeforeEach
    void setUp() {
        Reporte reporte = new Reporte();
        reporte.setDescripcion("Perdido cerca de plaza");
        reporte.setLatitud(-33.4489);
        reporte.setLongitud(-70.6693);
        reporte.setTipo(Reporte.TipoReporte.PERDIDO);
        reporte.setEstadoReporte(Reporte.EstadoReporte.ACTIVO);
        reporte.setFechaReporte(java.time.LocalDateTime.now());
        reporte.setUsuarioId(1);
        reporte.setMascotaId(1);
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
        assertThat(match.get(0).getMascotaId()).isEqualTo(1);
    }

    @Test
    @DisplayName("existeReporteActivoPorMascota: Debería retornar true si hay un reporte activo")
    void existeReporteActivoPorMascota_RetornaTrue() {
        boolean existe = reporteRepository.existeReporteActivoPorMascotaId(1, Reporte.EstadoReporte.ACTIVO);
        assertThat(existe).isTrue();
    }
}
