package com.mascotas.mascotas.service;

import com.mascotas.mascotas.model.Mascota;
import com.mascotas.mascotas.model.Usuario;
import com.mascotas.mascotas.dto.MascotaCreateDTO;
import com.mascotas.mascotas.dto.MascotaDTO;
import com.mascotas.mascotas.dto.MascotaUpdateDTO;
import com.mascotas.mascotas.repository.UsuarioRepository;
import com.mascotas.mascotas.repository.MascotaRepository;
import com.mascotas.mascotas.exception.BusinessRuleException;
import com.mascotas.mascotas.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class MascotaService {
    
    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private MascotaRepository mascotaRepository;

    //listar y buscar

    public List<MascotaDTO> listarmascotas() {
        return mascotaRepository.findAll()
        .stream()
        .map(this::convertirADto) // Convertimos a DTO para no exponer toda la info
        .toList();
    }

    public Optional<MascotaDTO> buscarPorId(Integer id) {
        return mascotaRepository.findById(id)
        .map(this::convertirADto); // Convertimos a DTO para no exponer toda la info
    }

    public List<MascotaDTO> buscarPorEspecie(String especieString) {
        if (especieString == null || especieString.trim().isEmpty()) {
            throw new BusinessRuleException("El parámetro de especie es obligatorio.");
        }
        try {
            // Convierte el texto (ej. "perro") al enum PERRO, controlando si el cliente se equivoca
            Mascota.Especie especieEnum = Mascota.Especie.valueOf(especieString.toUpperCase());
            return mascotaRepository.findByEspecie(especieEnum)
            .stream()
            .map(this::convertirADto) // Convertimos a DTO para no exponer toda la info
            .toList();
        } catch (IllegalArgumentException e) {
            throw new BusinessRuleException("Especie de mascota no válida. Las opciones son: PERRO, GATO, OTRO.");
        }
    }

    public List<MascotaDTO> buscarPorTamaño(String tamañoString) {
        if (tamañoString == null || tamañoString.trim().isEmpty()) {
            throw new BusinessRuleException("El parámetro de tamaño es obligatorio.");
        }
        try {
            // Convierte el texto (ej. "perro") al enum PERRO, controlando si el cliente se equivoca
            Mascota.Tamaño tamañoEnum = Mascota.Tamaño.valueOf(tamañoString.toUpperCase());
            return mascotaRepository.findByTamaño(tamañoEnum)
            .stream()
            .map(this::convertirADto) // Convertimos a DTO para no exponer toda la info
            .toList();
        } catch (IllegalArgumentException e) {
            throw new BusinessRuleException("tamaño de mascota no válida. Las opciones son: pequeño, mediano, grande.");
        }
    }

    public Optional<MascotaDTO> buscarPorChip(String chip) {
        return mascotaRepository.findByChipMascota(chip)
        .map(this::convertirADto); // Convertimos a DTO para no exponer toda la info
    }

    //registro

    @Transactional
    public MascotaDTO registrarMascota(MascotaCreateDTO request, String emailUsuario) {
        // 1. Buscamos al dueño real por el email del token
    Usuario dueño = usuarioRepository.findByEmail(emailUsuario)
            .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        
        // 2. Opcional: Validar que el chip no esté registrado ya en el sistema (solo si no es nulo y no está vacío)
        if (request.getChipMascota() != null && !request.getChipMascota().trim().isEmpty() && mascotaRepository.findByChipMascota(request.getChipMascota()).isPresent()) {
            throw new BusinessRuleException("Ya existe una mascota registrada con este chip.");
        }
        
        // 3. Crear nueva instancia
        Mascota mascota = new Mascota();
        mascota.setChipMascota(request.getChipMascota());
        mascota.setNombreMascota(request.getNombreMascota());
        mascota.setEspecie(Mascota.Especie.valueOf(request.getEspecie().toUpperCase()));
        mascota.setTamaño(Mascota.Tamaño.valueOf(request.getTamaño().toUpperCase()));
        
        mascota.setRaza(request.getRaza());
        mascota.setSexo(request.getSexo());
        mascota.setColor(request.getColor());
        
        // Asignamos el dueño
        mascota.setUsuario(dueño);
        
        return convertirADto(mascotaRepository.save(mascota));
    }

    //actualizacion

    @Transactional
    public MascotaDTO actualizarMascota(Integer id, MascotaUpdateDTO request, String emailUsuario) {
        Mascota mascota = mascotaRepository.findById(id)
            .orElseThrow(()-> new ResourceNotFoundException("Mascota no encontrada con ID: " + id));

        // VALIDACIÓN DE SEGURIDAD para saber si Es el dueño
        if (!mascota.getUsuario().getEmail().equals(emailUsuario)) {
            throw new BusinessRuleException("No tienes permiso para editar esta mascota.");
        }
        //solo los campos que se deban actualizar
        mascota.setNombreMascota(request.getNombre());
        mascota.setRaza(request.getRaza());
        mascota.setSexo(request.getSexo());
        mascota.setColor(request.getColor());
        mascota.setEspecie(Mascota.Especie.valueOf(request.getEspecie().toUpperCase()));
        mascota.setTamaño(Mascota.Tamaño.valueOf(request.getTamaño().toUpperCase()));

        return convertirADto(mascotaRepository.save(mascota));
    }

    //eliminar

    @Transactional
    public void eliminarMascota(Integer id, String emailUsuario) {
        Mascota mascota = mascotaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mascota no encontrada"));

        // VALIDACIÓN DE SEGURIDAD: ¿Es el dueño?
        if (!mascota.getUsuario().getEmail().equals(emailUsuario)) {
            throw new BusinessRuleException("No tienes permiso para eliminar esta mascota.");
        }

        mascotaRepository.delete(mascota);
    }

    private MascotaDTO convertirADto(Mascota mascota) {
        MascotaDTO dto = new MascotaDTO();
        dto.setIdMascota(mascota.getIdMascota());
        dto.setChipMascota(mascota.getChipMascota());
        dto.setNombreMascota(mascota.getNombreMascota());
        dto.setEspecie(mascota.getEspecie().name());
        dto.setRaza(mascota.getRaza());
        dto.setSexo(mascota.getSexo());
        dto.setTamaño(mascota.getTamaño().name());
        dto.setColor(mascota.getColor());
        return dto;
    }
}