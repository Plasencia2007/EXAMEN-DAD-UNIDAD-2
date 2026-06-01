package com.plasencia.ms_gestion_instructor.service;

import com.plasencia.ms_gestion_instructor.entity.Instructor;
import com.plasencia.ms_gestion_instructor.exception.RecursoNoEncontradoException;
import com.plasencia.ms_gestion_instructor.repository.InstructorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InstructorService {

    private final InstructorRepository instructorRepository;

    public List<Instructor> listar() {
        return instructorRepository.findAll();
    }

    public Instructor buscarPorId(Long id) {
        return instructorRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No se encontro el instructor con id " + id));
    }

    public Instructor guardar(Instructor instructor) {
        return instructorRepository.save(instructor);
    }

    public Instructor actualizar(Long id, Instructor datos) {
        Instructor instructor = buscarPorId(id);
        instructor.setNombres(datos.getNombres());
        instructor.setApellidos(datos.getApellidos());
        instructor.setEspecialidad(datos.getEspecialidad());
        instructor.setCorreo(datos.getCorreo());
        instructor.setTelefono(datos.getTelefono());
        return instructorRepository.save(instructor);
    }

    public void eliminar(Long id) {
        Instructor instructor = buscarPorId(id);
        instructorRepository.delete(instructor);
    }
}
