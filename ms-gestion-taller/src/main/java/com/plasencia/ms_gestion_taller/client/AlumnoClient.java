package com.plasencia.ms_gestion_taller.client;

import com.plasencia.ms_gestion_taller.dto.AlumnoDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * Cliente OpenFeign hacia el microservicio de alumnos.
 */
@FeignClient(name = "ms-gestion-alumno", path = "/api/alumnos")
public interface AlumnoClient {

    @GetMapping("/{id}")
    AlumnoDTO obtenerAlumno(@PathVariable("id") Long id);

    @GetMapping("/por-ids")
    List<AlumnoDTO> obtenerAlumnosPorIds(@RequestParam("ids") List<Long> ids);
}
