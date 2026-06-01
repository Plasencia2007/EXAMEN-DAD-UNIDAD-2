package com.plasencia.ms_gestion_taller.service;

import com.plasencia.ms_gestion_taller.dto.AlumnoDTO;
import com.plasencia.ms_gestion_taller.entity.Inscripcion;
import com.plasencia.ms_gestion_taller.exception.RecursoNoEncontradoException;
import com.plasencia.ms_gestion_taller.exception.ReglaNegocioException;
import com.plasencia.ms_gestion_taller.exception.ServicioNoDisponibleException;
import com.plasencia.ms_gestion_taller.gateway.AlumnoGateway;
import com.plasencia.ms_gestion_taller.repository.InscripcionRepository;
import com.plasencia.ms_gestion_taller.repository.TallerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * CRUD explicito de la tabla {@code inscripcion}.
 * Para la creacion reutiliza la logica de negocio de {@link TallerService}
 * (validacion de cupo, duplicados y existencia de alumno/taller via OpenFeign).
 */
@Service
@RequiredArgsConstructor
public class InscripcionService {

    private final InscripcionRepository inscripcionRepository;
    private final TallerRepository tallerRepository;
    private final TallerService tallerService;
    private final AlumnoGateway alumnoGateway;

    public List<Inscripcion> listar() {
        return inscripcionRepository.findAll();
    }

    public Inscripcion buscarPorId(Long id) {
        return inscripcionRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No se encontro la inscripcion con id " + id));
    }

    public List<Inscripcion> listarPorTaller(Long idTaller) {
        return inscripcionRepository.findByIdTaller(idTaller);
    }

    public Inscripcion crear(Inscripcion inscripcion) {
        // Reutiliza la regla de negocio completa (cupo, duplicado, existencia)
        return tallerService.inscribirAlumno(inscripcion.getIdTaller(), inscripcion.getIdAlumno());
    }

    public Inscripcion actualizar(Long id, Inscripcion datos) {
        Inscripcion inscripcion = buscarPorId(id);

        // El taller destino debe existir
        tallerRepository.findById(datos.getIdTaller())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe el taller con id " + datos.getIdTaller()));

        // El alumno debe existir (microservicio de alumnos)
        validarAlumnoExiste(datos.getIdAlumno());

        // No puede quedar duplicada con OTRA inscripcion ya existente
        boolean duplicada = inscripcionRepository
                .existsByIdTallerAndIdAlumno(datos.getIdTaller(), datos.getIdAlumno())
                && !(inscripcion.getIdTaller().equals(datos.getIdTaller())
                && inscripcion.getIdAlumno().equals(datos.getIdAlumno()));
        if (duplicada) {
            throw new ReglaNegocioException(
                    "El alumno " + datos.getIdAlumno()
                            + " ya esta inscrito en el taller " + datos.getIdTaller());
        }

        inscripcion.setIdTaller(datos.getIdTaller());
        inscripcion.setIdAlumno(datos.getIdAlumno());
        return inscripcionRepository.save(inscripcion);
    }

    public void eliminar(Long id) {
        Inscripcion inscripcion = buscarPorId(id);
        inscripcionRepository.delete(inscripcion);
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
