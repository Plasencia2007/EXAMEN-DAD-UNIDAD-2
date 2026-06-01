package com.plasencia.ms_gestion_alumno.repository;

import com.plasencia.ms_gestion_alumno.entity.Alumno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlumnoRepository extends JpaRepository<Alumno, Long> {

    // Permite al microservicio de talleres pedir varios alumnos por sus IDs
    List<Alumno> findByIdIn(List<Long> ids);
}
