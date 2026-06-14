package com.mascotas.mascotas.repository;

import com.mascotas.mascotas.model.Reporte;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ReporteRepository extends JpaRepository<Reporte, Integer> {
    
    @Query("SELECT DISTINCT r FROM Reporte r LEFT JOIN FETCH r.urlsFotos")
    List<Reporte> findAllConDetalles();
    
    @Query("SELECT DISTINCT r FROM Reporte r LEFT JOIN FETCH r.urlsFotos WHERE r.tipo = :tipo")
    List<Reporte> findByTipo(@Param("tipo") Reporte.TipoReporte tipo);

    @Query("SELECT DISTINCT r FROM Reporte r LEFT JOIN FETCH r.urlsFotos WHERE r.tipo = :tipo AND r.estadoReporte = :estado")
    List<Reporte> findByTipoAndEstadoReporte(@Param("tipo") Reporte.TipoReporte tipo, @Param("estado") Reporte.EstadoReporte estado);

    @Query("SELECT COUNT(r) > 0 FROM Reporte r WHERE r.mascotaId = :mascotaId AND r.estadoReporte = :estado")
    boolean existeReporteActivoPorMascotaId(
        @Param("mascotaId") Integer mascotaId, 
        @Param("estado") Reporte.EstadoReporte estado
    );

    @Query("SELECT DISTINCT r FROM Reporte r LEFT JOIN FETCH r.urlsFotos WHERE r.mascotaId IN :mascotaIds AND r.tipo = :tipo")
    List<Reporte> findByMascotaIdInAndTipo(@Param("mascotaIds") List<Integer> mascotaIds, @Param("tipo") Reporte.TipoReporte tipo);
}
