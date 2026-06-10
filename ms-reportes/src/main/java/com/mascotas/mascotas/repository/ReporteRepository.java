package com.mascotas.mascotas.repository;

import com.mascotas.mascotas.model.Reporte;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ReporteRepository extends JpaRepository<Reporte, Integer> {
    
    @Query("SELECT r FROM Reporte r")
    List<Reporte> findAllConDetalles();
    
    List<Reporte> findByTipo(Reporte.TipoReporte tipo);

    List<Reporte> findByTipoAndEstadoReporte(Reporte.TipoReporte tipo, Reporte.EstadoReporte estado);

    @Query("SELECT COUNT(r) > 0 FROM Reporte r WHERE r.mascotaId = :mascotaId AND r.estadoReporte = :estado")
    boolean existeReporteActivoPorMascotaId(
        @Param("mascotaId") Integer mascotaId, 
        @Param("estado") Reporte.EstadoReporte estado
    );

    List<Reporte> findByMascotaIdInAndTipo(List<Integer> mascotaIds, Reporte.TipoReporte tipo);
}
