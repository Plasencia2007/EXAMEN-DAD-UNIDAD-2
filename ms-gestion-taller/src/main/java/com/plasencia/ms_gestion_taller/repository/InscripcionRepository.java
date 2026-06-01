package com.plasencia.ms_gestion_taller.repository;

import com.plasencia.ms_gestion_taller.entity.Inscripcion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InscripcionRepository extends JpaRepository<Inscripcion, Long> {

    List<Inscripcion> findByIdTaller(Long idTaller);

    boolean existsByIdTallerAndIdAlumno(Long idTaller, Long idAlumno);

    long countByIdTaller(Long idTaller);
}
