package com.mascotas.mascotas.service;

import com.mascotas.mascotas.model.Reporte;
import com.mascotas.mascotas.dto.ReporteCreateDTO;
import com.mascotas.mascotas.dto.ReporteDTO;
import com.mascotas.mascotas.dto.ReporteUpdateDTO;
import com.mascotas.mascotas.exception.BusinessRuleException;
import com.mascotas.mascotas.exception.ResourceNotFoundException;
import com.mascotas.mascotas.repository.ReporteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ReporteService {

    @Autowired
    private ReporteRepository reporteRepository;

    @Autowired
    private RestTemplate restTemplate;

    private Integer getUsuarioIdAutenticado() {
        Object principal = org.springframework.security.core.context.SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        if (principal instanceof com.mascotas.mascotas.security.CustomUserDetails) {
            return ((com.mascotas.mascotas.security.CustomUserDetails) principal).getIdUsuario();
        }
        throw new BusinessRuleException("Usuario no autenticado");
    }

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
            Reporte.TipoReporte tipoEnum = Reporte.TipoReporte.valueOf(tipoString.toUpperCase());
            return reporteRepository.findByTipo(tipoEnum)
                    .stream()
                    .map(this::convertirADto) // Convertimos a DTO para no exponer toda la info
                    .toList();
        } catch (IllegalArgumentException e) {
            throw new BusinessRuleException(
                    "Tipo de reporte no válido. Las opciones son: ENCONTRADO, PERDIDO, AVISTADA.");
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
            Reporte.TipoReporte tipoEnum = Reporte.TipoReporte.valueOf(tipoString.toUpperCase());

            // Obtener los IDs de mascotas correspondientes a la especie desde ms-mascotas
            List<Integer> mascotaIds = new java.util.ArrayList<>();
            try {
                String url = "http://localhost:8082/api/mascota/especie/" + especieString.toLowerCase();
                com.mascotas.mascotas.dto.MascotaDTO[] mascotas = restTemplate.getForObject(url,
                        com.mascotas.mascotas.dto.MascotaDTO[].class);
                if (mascotas != null) {
                    for (com.mascotas.mascotas.dto.MascotaDTO m : mascotas) {
                        mascotaIds.add(m.getIdMascota());
                    }
                }
            } catch (org.springframework.web.client.HttpClientErrorException.NotFound e) {
                // Si no hay mascotas de esa especie, no hay reportes de esa especie
                return List.of();
            } catch (Exception e) {
                throw new RuntimeException("Error al comunicarse con el servicio de Mascotas", e);
            }

            if (mascotaIds.isEmpty()) {
                return List.of();
            }

            return reporteRepository.findByMascotaIdInAndTipo(mascotaIds, tipoEnum)
                    .stream()
                    .map(this::convertirADto)
                    .toList();
        } catch (IllegalArgumentException e) {
            throw new BusinessRuleException(
                    "Especie o Tipo no válido. Especies: PERRO, GATO, OTRO. Tipos: ENCONTRADO, PERDIDO, AVISTADA.");
        }
    }

    public List<ReporteDTO> buscarTipoYEstadoReporte(String tipo, String estado) {
        if (tipo == null || tipo.trim().isEmpty()) {
            throw new BusinessRuleException("El parámetro de tipo es obligatorio.");
        }
        if (estado == null || estado.trim().isEmpty()) {
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
            throw new BusinessRuleException(
                    "Tipo de reporte no válido. Las opciones son: ENCONTRADO, PERDIDO, AVISTADA.");
        }
    }

    // --- CREACIÓN ---

    @Transactional
    public ReporteDTO registrarReporte(ReporteCreateDTO request, String emailUsuario) {
        // 1. Validar que la mascota no tenga ya un reporte activo en el sistema
        if (reporteRepository.existeReporteActivoPorMascotaId(request.getMascotaId(), Reporte.EstadoReporte.ACTIVO)) {
            throw new BusinessRuleException("Esta mascota ya tiene un reporte ACTIVO en el sistema.");
        }

        // 2. Crear el nuevo reporte
        Reporte reporte = new Reporte();
        reporte.setTipo(Reporte.TipoReporte.valueOf(request.getTipo().toUpperCase()));
        reporte.setEstadoReporte(Reporte.EstadoReporte.valueOf(request.getEstado().toUpperCase()));
        reporte.setDescripcion(request.getDescripcion());
        reporte.setLatitud(request.getLatitud());
        reporte.setLongitud(request.getLongitud());
        reporte.setUrlsFotos(request.getUrlsFotos());

        reporte.setUsuarioId(getUsuarioIdAutenticado());
        reporte.setMascotaId(request.getMascotaId());
        reporte.setFechaReporte(LocalDateTime.now());
        return convertirADto(reporteRepository.save(reporte));
    }

    // --- ACTUALIZACIÓN ---

    @Transactional
    public ReporteDTO actualizarReporte(Integer id, ReporteUpdateDTO request, String emailUsuario) {
        Reporte reporte = reporteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reporte no encontrado con ID: " + id));

        // Validación Zero Trust: comprobar que el usuario logueado es el dueño del
        // reporte
        if (!reporte.getUsuarioId().equals(getUsuarioIdAutenticado())) {
            throw new BusinessRuleException("No tienes permisos para modificar este reporte.");
        }

        reporte.setTipo(Reporte.TipoReporte.valueOf(request.getTipo().toUpperCase()));
        reporte.setEstadoReporte(Reporte.EstadoReporte.valueOf(request.getEstado().toUpperCase()));
        reporte.setDescripcion(request.getDescripcion());
        reporte.setLatitud(request.getLatitud());
        reporte.setLongitud(request.getLongitud());

        return convertirADto(reporteRepository.save(reporte));
    }

    // --- ELIMINAR ---

    @Transactional
    public void eliminarReporte(Integer id, String emailUsuario) {
        Reporte reporte = reporteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reporte no encontrado con ID: " + id));

        // Validación Zero Trust: comprobar que el usuario logueado es el dueño del
        // reporte
        if (!reporte.getUsuarioId().equals(getUsuarioIdAutenticado())) {
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

        // coordenadas
        dto.setLatitud(reporte.getLatitud());
        dto.setLongitud(reporte.getLongitud());

        // IDs decoupled
        dto.setUsuarioId(reporte.getUsuarioId());
        dto.setMascotaId(reporte.getMascotaId());

        if (reporte.getUrlsFotos() != null && !reporte.getUrlsFotos().isEmpty()) {
            List<String> fotosConUrlCompleta = reporte.getUrlsFotos().stream()
                    .map(nombreArchivo -> {
                        if (nombreArchivo.startsWith("http://") || nombreArchivo.startsWith("https://")) {
                            return nombreArchivo;
                        }
                        if (org.springframework.web.context.request.RequestContextHolder
                                .getRequestAttributes() != null) {
                            return org.springframework.web.servlet.support.ServletUriComponentsBuilder
                                    .fromCurrentContextPath()
                                    .path("/uploads/")
                                    .path(nombreArchivo)
                                    .toUriString();
                        } else {
                            return "http://localhost:8083/uploads/" + nombreArchivo;
                        }
                    }).toList();
            dto.setUrlsFotos(fotosConUrlCompleta);
        } else {
            dto.setUrlsFotos(new java.util.ArrayList<>());
        }

        return dto;
    }
}
