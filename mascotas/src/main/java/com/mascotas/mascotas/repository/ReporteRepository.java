package com.mascotas.mascotas.repository;

import com.mascotas.mascotas.model.Mascota;
import com.mascotas.mascotas.model.Reporte;
import org.springframework.data.jpa.repository.JpaRepository;//import que ofrece los métodos CRUD
import org.springframework.data.jpa.repository.Query;//permite escribir consultas personalizadas
import org.springframework.data.repository.query.Param;//permite escribir consultas personalizadas
import org.springframework.stereotype.Repository;//marca esta clase interfaz como repositorio de spring
import java.util.List;

@Repository
public interface ReporteRepository extends JpaRepository<Reporte, Integer> {
    
    // Solución N+1: Query con JOIN FETCH para traer las cruces de mascota y usuario en una sola consulta SQL
    @Query("SELECT r FROM Reporte r JOIN FETCH r.usuario JOIN FETCH r.mascota")
    List<Reporte> findAllConDetalles();
    
    // 1. Método "Mágico" de Spring Data: Busca por el enum TipoReporte (PERDIDO, ENCONTRADO, AVISTADA)
    List<Reporte> findByTipo(Reporte.TipoReporte tipo);

    // 2. Búsqueda combinada: Solo reportes de un tipo que estén ACTIVO
    // Esto es vital para no mostrar reportes viejos o resueltos en el mapa
    List<Reporte> findByTipoAndEstadoReporte(Reporte.TipoReporte tipo, Reporte.EstadoReporte estado);

    // Consulta alternativa: verificar si una mascota tiene un reporte específicamente "ACTIVO"
    @Query("SELECT COUNT(r) > 0 FROM Reporte r WHERE r.mascota = :mascota AND r.estadoReporte = :estado")
    boolean existeReporteActivoPorMascota(
        @org.springframework.data.repository.query.Param("mascota") com.mascotas.mascotas.model.Mascota mascota, 
        @org.springframework.data.repository.query.Param("estado") com.mascotas.mascotas.model.Reporte.EstadoReporte estado
    );

    // 3. Consulta JPQL personalizada: Buscar reportes por especie de mascota
    // Esto lo usará el Motor de Coincidencias para no comparar un perro con un gato
    @Query("SELECT r FROM Reporte r JOIN r.mascota m WHERE m.especie = :especie AND r.tipo = :tipo")
    List<Reporte> buscarPorEspecieYTipo(@Param("especie") Mascota.Especie especie, @Param("tipo") Reporte.TipoReporte tipo);
}
