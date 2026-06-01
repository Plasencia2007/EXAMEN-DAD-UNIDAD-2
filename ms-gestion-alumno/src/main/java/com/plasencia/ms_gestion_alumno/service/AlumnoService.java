package com.plasencia.ms_gestion_alumno.service;

import com.plasencia.ms_gestion_alumno.entity.Alumno;
import com.plasencia.ms_gestion_alumno.exception.RecursoNoEncontradoException;
import com.plasencia.ms_gestion_alumno.repository.AlumnoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AlumnoService {

    private final AlumnoRepository alumnoRepository;

    public List<Alumno> listar() {
        return alumnoRepository.findAll();
    }

    public Alumno buscarPorId(Long id) {
        return alumnoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No se encontro el alumno con id " + id));
    }

    public List<Alumno> buscarPorIds(List<Long> ids) {
        return alumnoRepository.findByIdIn(ids);
    }

    public Alumno guardar(Alumno alumno) {
        return alumnoRepository.save(alumno);
    }

    public Alumno actualizar(Long id, Alumno datos) {
        Alumno alumno = buscarPorId(id);
        alumno.setNombres(datos.getNombres());
        alumno.setApellidos(datos.getApellidos());
        alumno.setDni(datos.getDni());
        alumno.setCorreo(datos.getCorreo());
        alumno.setTelefono(datos.getTelefono());
        return alumnoRepository.save(alumno);
    }

    public void eliminar(Long id) {
        Alumno alumno = buscarPorId(id);
        alumnoRepository.delete(alumno);
    }
}
