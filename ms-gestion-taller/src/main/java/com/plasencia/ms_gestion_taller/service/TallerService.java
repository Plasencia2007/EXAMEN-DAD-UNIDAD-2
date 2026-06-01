package com.plasencia.ms_gestion_taller.service;

import com.plasencia.ms_gestion_taller.dto.AlumnoDTO;
import com.plasencia.ms_gestion_taller.dto.InstructorDTO;
import com.plasencia.ms_gestion_taller.dto.TallerDetalleDTO;
import com.plasencia.ms_gestion_taller.entity.Inscripcion;
import com.plasencia.ms_gestion_taller.entity.Taller;
import com.plasencia.ms_gestion_taller.exception.RecursoNoEncontradoException;
import com.plasencia.ms_gestion_taller.exception.ReglaNegocioException;
import com.plasencia.ms_gestion_taller.exception.ServicioNoDisponibleException;
import com.plasencia.ms_gestion_taller.gateway.AlumnoGateway;
import com.plasencia.ms_gestion_taller.gateway.InstructorGateway;
import com.plasencia.ms_gestion_taller.repository.InscripcionRepository;
import com.plasencia.ms_gestion_taller.repository.TallerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TallerService {

    private final TallerRepository tallerRepository;
    private final InscripcionRepository inscripcionRepository;
    // Gateways resilientes (Circuit Breaker + Retry) en vez de los clientes Feign directos
    private final InstructorGateway instructorGateway;
    private final AlumnoGateway alumnoGateway;

    // ---------- CRUD basico del taller ----------

    public List<Taller> listar() {
        return tallerRepository.findAll();
    }

    public Taller buscarPorId(Long id) {
        return tallerRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No se encontro el taller con id " + id));
    }

    public Taller crear(Taller taller) {
        // Valida que el instructor exista en el otro microservicio antes de crear
        validarInstructorExiste(taller.getIdInstructor());
        return tallerRepository.save(taller);
    }

    public Taller actualizar(Long id, Taller datos) {
        Taller taller = buscarPorId(id);
        validarInstructorExiste(datos.getIdInstructor());
        taller.setNombre(datos.getNombre());
        taller.setDescripcion(datos.getDescripcion());
        taller.setFechaInicio(datos.getFechaInicio());
        taller.setCupoMaximo(datos.getCupoMaximo());
        taller.setIdInstructor(datos.getIdInstructor());
        return tallerRepository.save(taller);
    }

    public void eliminar(Long id) {
        Taller taller = buscarPorId(id);
        inscripcionRepository.deleteAll(inscripcionRepository.findByIdTaller(id));
        tallerRepository.delete(taller);
    }

    // ---------- Inscripcion de alumnos (logica compuesta) ----------

    public Inscripcion inscribirAlumno(Long idTaller, Long idAlumno) {
        Taller taller = buscarPorId(idTaller);

        // 1) El alumno debe existir (se consulta al microservicio de alumnos)
        validarAlumnoExiste(idAlumno);

        // 2) No puede inscribirse dos veces en el mismo taller
        if (inscripcionRepository.existsByIdTallerAndIdAlumno(idTaller, idAlumno)) {
            throw new ReglaNegocioException(
                    "El alumno " + idAlumno + " ya esta inscrito en el taller " + idTaller);
        }

        // 3) Debe haber cupo disponible
        long inscritos = inscripcionRepository.countByIdTaller(idTaller);
        if (inscritos >= taller.getCupoMaximo()) {
            throw new ReglaNegocioException(
                    "El taller " + idTaller + " ya alcanzo su cupo maximo (" + taller.getCupoMaximo() + ")");
        }

        Inscripcion inscripcion = Inscripcion.builder()
                .idTaller(idTaller)
                .idAlumno(idAlumno)
                .build();
        return inscripcionRepository.save(inscripcion);
    }

    public void cancelarInscripcion(Long idTaller, Long idAlumno) {
        Inscripcion inscripcion = inscripcionRepository.findByIdTaller(idTaller).stream()
                .filter(i -> i.getIdAlumno().equals(idAlumno))
                .findFirst()
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "El alumno " + idAlumno + " no esta inscrito en el taller " + idTaller));
        inscripcionRepository.delete(inscripcion);
    }

    /**
     * Devuelve la vista COMPUESTA del taller: sus datos + el instructor completo
     * + la lista de alumnos inscritos, todo reunido mediante OpenFeign.
     * <p>
     * Camino de LECTURA: degradacion elegante. Si un servicio no responde, se
     * muestra un placeholder (instructor "No disponible" / lista de alumnos vacia)
     * en vez de fallar toda la peticion.
     */
    public TallerDetalleDTO obtenerDetalle(Long idTaller) {
        Taller taller = buscarPorId(idTaller);

        // Instructor (resiliente). null = el instructor ya no existe (404)
        InstructorDTO instructor = instructorGateway.obtenerInstructor(taller.getIdInstructor());
        if (instructor == null) {
            instructor = InstructorDTO.builder()
                    .id(taller.getIdInstructor())
                    .nombres("Instructor no encontrado")
                    .build();
        }

        // Alumnos inscritos (resiliente). Si el servicio cae, llega lista vacia
        List<Long> idsAlumnos = inscripcionRepository.findByIdTaller(idTaller).stream()
                .map(Inscripcion::getIdAlumno)
                .toList();

        List<AlumnoDTO> alumnos = idsAlumnos.isEmpty()
                ? Collections.emptyList()
                : alumnoGateway.obtenerAlumnosPorIds(idsAlumnos);

        return TallerDetalleDTO.builder()
                .taller(taller)
                .instructor(instructor)
                .alumnosInscritos(alumnos)
                .cuposDisponibles(taller.getCupoMaximo() - idsAlumnos.size())
                .build();
    }

    // ---------- Validaciones contra otros microservicios (camino de ESCRITURA) ----------
    // Aqui NO degradamos: si no se puede verificar al instructor/alumno, fallamos.

    private void validarInstructorExiste(Long idInstructor) {
        InstructorDTO instructor = instructorGateway.obtenerInstructor(idInstructor);
        if (instructor == null) {
            throw new RecursoNoEncontradoException("No existe el instructor con id " + idInstructor);
        }
        if (Boolean.FALSE.equals(instructor.getDisponible())) {
            throw new ServicioNoDisponibleException(
                    "El servicio de instructores no esta disponible, intente mas tarde");
        }
    }

    private void validarAlumnoExiste(Long idAlumno) {
        AlumnoDTO alumno = alumnoGateway.obtenerAlumno(idAlumno);
        if (alumno == null) {
            throw new RecursoNoEncontradoException("No existe el alumno con id " + idAlumno);
        }
        if (Boolean.FALSE.equals(alumno.getDisponible())) {
            throw new ServicioNoDisponibleException(
                    "El servicio de alumnos no esta disponible, intente mas tarde");
        }
    }
}
