package com.mascotas.mascotas.service;

import com.mascotas.mascotas.model.Mascota;
import com.mascotas.mascotas.model.Reporte;
import com.mascotas.mascotas.model.Usuario;
import com.mascotas.mascotas.dto.ReporteCreateDTO;
import com.mascotas.mascotas.dto.ReporteDTO;
import com.mascotas.mascotas.dto.ReporteUpdateDTO;
import com.mascotas.mascotas.exception.BusinessRuleException;
import com.mascotas.mascotas.exception.ResourceNotFoundException;
import com.mascotas.mascotas.repository.ReporteRepository;
import com.mascotas.mascotas.repository.UsuarioRepository;
import com.mascotas.mascotas.repository.MascotaRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ReporteService {

    @Autowired
    private ReporteRepository reporteRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private MascotaRepository mascotaRepository;

    // --- LISTAR Y BUSCAR ---

    public List<ReporteDTO> listarReportes() {
        return reporteRepository.findAllConDetalles()
        .stream()
        .map(this::convertirADto) // Convertimos a DTO para no exponer toda la info
        .toList();
    }

    public Optional<ReporteDTO> buscarPorId(Integer id) {
        return reporteRepository.findById(id)
        .map(this::convertirADto); // Convertimos a DTO para no exponer toda la info
    }
    
    public List<ReporteDTO> buscarPorTipo(String tipoString) {
        if (tipoString == null || tipoString.trim().isEmpty()) {
            throw new BusinessRuleException("El parámetro de tipo de reporte es obligatorio.");
        }
        try {
            // Convierte el texto (ej. "perdido") al enum PERDIDO, controlando si el cliente se equivoca
            Reporte.TipoReporte tipoEnum = Reporte.TipoReporte.valueOf(tipoString.toUpperCase());
            return reporteRepository.findByTipo(tipoEnum)
            .stream()
            .map(this::convertirADto) // Convertimos a DTO para no exponer toda la info
            .toList();
        } catch (IllegalArgumentException e) {
            throw new BusinessRuleException("Tipo de reporte no válido. Las opciones son: ENCONTRADO, PERDIDO, AVISTADA.");
        }
    }

    public List<ReporteDTO> buscarPorEspecieYTipo(String especieString, String tipoString) {
        if (especieString == null || especieString.trim().isEmpty()) {
            throw new BusinessRuleException("El parámetro de especie es obligatorio.");
        }
        if (tipoString == null || tipoString.trim().isEmpty()) {
            throw new BusinessRuleException("El parámetro de tipo de reporte es obligatorio.");
        }
        try {
            // Convertimos AMBOS Strings a sus respectivos Enums antes de consultar la BD
            Reporte.TipoReporte tipoEnum = Reporte.TipoReporte.valueOf(tipoString.toUpperCase());
            Mascota.Especie especieEnum = Mascota.Especie.valueOf(especieString.toUpperCase());
            
            // Le pasamos los Enums al repositorio
            return reporteRepository.buscarPorEspecieYTipo(especieEnum, tipoEnum)
            .stream()
            .map(this::convertirADto)
            .toList();
        } catch (IllegalArgumentException e) {
            throw new BusinessRuleException("Especie o Tipo no válido. Especies: PERRO, GATO, OTRO. Tipos: ENCONTRADO, PERDIDO, AVISTADA.");
        }
    }

    public List<ReporteDTO> buscarTipoYEstadoReporte(String tipo, String estado) {
        if (tipo == null || tipo.trim().isEmpty()){
            throw new BusinessRuleException("El parámetro de tipo es obligatorio.");
        }
        if (estado == null || estado.trim().isEmpty()){
            throw new BusinessRuleException("El parámetro de estado es obligatorio.");
        }
        try {
            Reporte.TipoReporte tipoEnum = Reporte.TipoReporte.valueOf(tipo.toUpperCase());
            Reporte.EstadoReporte estadoEnum = Reporte.EstadoReporte.valueOf(estado.toUpperCase());
            return reporteRepository.findByTipoAndEstadoReporte(tipoEnum, estadoEnum)
            .stream()
            .map(this::convertirADto)
            .toList();
        } catch (IllegalArgumentException e) {
            throw new BusinessRuleException("Tipo de reporte no válido. Las opciones son: ENCONTRADO, PERDIDO, AVISTADA.");
        }
    }
    
    // --- CREACIÓN ---

    @Transactional
    public ReporteDTO registrarReporte(ReporteCreateDTO request, String emailUsuario) {
        // 1. Validar que el usuario y mascota existan
        Usuario usuario = usuarioRepository.findByEmail(emailUsuario)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con email: " + emailUsuario));
        
        Mascota mascota = mascotaRepository.findById(request.getMascotaId())
                .orElseThrow(() -> new ResourceNotFoundException("Mascota no encontrada con ID: " + request.getMascotaId()));
        
        // 2. Validar que la mascota no tenga ya un reporte activo en el sistema
        if (reporteRepository.existeReporteActivoPorMascota(mascota, Reporte.EstadoReporte.ACTIVO)) {
            throw new BusinessRuleException("Esta mascota ya tiene un reporte ACTIVO en el sistema.");
        }

        // 3. Crear el nuevo reporte
        Reporte reporte = new Reporte();
        reporte.setTipo(Reporte.TipoReporte.valueOf(request.getTipo().toUpperCase()));
        reporte.setEstadoReporte(Reporte.EstadoReporte.valueOf(request.getEstado().toUpperCase()));
        reporte.setDescripcion(request.getDescripcion());
        reporte.setLatitud(request.getLatitud());
        reporte.setLongitud(request.getLongitud());

        reporte.setUrlFoto(request.getUrlFoto());
        
        reporte.setUsuario(usuario);
        reporte.setMascota(mascota);
        reporte.setFechaReporte(LocalDateTime.now());
        return convertirADto(reporteRepository.save(reporte));
    }

    //actualizacion

    @Transactional
    public ReporteDTO actualizarReporte(Integer id, ReporteUpdateDTO request, String emailUsuario) {
        Reporte reporte = reporteRepository.findById(id)
            .orElseThrow(()-> new ResourceNotFoundException("Reporte no encontrado con ID: " + id));

        // Validación Zero Trust: comprobar que el usuario logueado es el dueño del reporte
        if (!reporte.getUsuario().getEmail().equals(emailUsuario)) {
            throw new BusinessRuleException("No tienes permisos para modificar este reporte.");
        }

        //solo los campos que se deban actualizar
        reporte.setTipo(Reporte.TipoReporte.valueOf(request.getTipo().toUpperCase()));
        reporte.setEstadoReporte(Reporte.EstadoReporte.valueOf(request.getEstado().toUpperCase()));
        reporte.setDescripcion(request.getDescripcion());
        reporte.setLatitud(request.getLatitud());
        reporte.setLongitud(request.getLongitud());
        // No actualizamos usuario ni mascota en esta función, para evitar cambios no deseados

        return convertirADto(reporteRepository.save(reporte));
    }

    //eliminar

    @Transactional
    public void eliminarReporte(Integer id, String emailUsuario) {
        Reporte reporte = reporteRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Reporte no encontrado con ID: " + id));
            
        // Validación Zero Trust: comprobar que el usuario logueado es el dueño del reporte
        if (!reporte.getUsuario().getEmail().equals(emailUsuario)) {
            throw new BusinessRuleException("No tienes permisos para eliminar este reporte.");
        }
        
        reporteRepository.deleteById(id);
    }
    
    private ReporteDTO convertirADto(Reporte reporte) {
        ReporteDTO dto = new ReporteDTO();
        dto.setIdReporte(reporte.getIdReporte());
        dto.setTipo(reporte.getTipo().name());
        dto.setEstado(reporte.getEstadoReporte().name());
        dto.setFecha(reporte.getFechaReporte());    
        dto.setDescripcion(reporte.getDescripcion());

        //coordenadas
        dto.setLatitud(reporte.getLatitud());
        dto.setLongitud(reporte.getLongitud());

        //usuario
        if (reporte.getUsuario() != null) {
            dto.setNombreContacto(reporte.getUsuario().getNombre());
            dto.setTelefonoContacto(reporte.getUsuario().getTelefono());
        }

        //mascota
        if(reporte.getMascota() != null) {
            dto.setNombreMascota(reporte.getMascota().getNombreMascota());
            dto.setRazaMascota(reporte.getMascota().getRaza());
            dto.setUrlFoto(reporte.getUrlFoto());
        }

        return dto;
    }
}
