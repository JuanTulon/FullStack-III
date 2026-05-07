package com.mascotas.mascotas.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.mascotas.mascotas.model.Mascota;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;//permite escribir consultas personalizadas
import org.springframework.data.repository.query.Param;//permite escribir consultas personalizadas
import org.springframework.stereotype.Repository;//marca esta clase interfaz como repositorio de spring
import java.util.List;

@Repository
public interface MascotaRepository extends JpaRepository<Mascota, Integer>{
    
    List<Mascota> findByEspecie(Mascota.Especie especie);

    List<Mascota> findByTamaño(Mascota.Tamaño tamaño);

    Optional<Mascota> findByChip_mascota(String chip_mascota);

}
