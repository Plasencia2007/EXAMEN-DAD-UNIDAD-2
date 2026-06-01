package com.plasencia.ms_seguridad.client;

import com.plasencia.ms_seguridad.dto.AlumnoFichaDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Cliente OpenFeign hacia el microservicio de alumnos. Se usa para crear la
 * ficha al registrar un ALUMNO y para eliminarla (compensacion Saga).
 */
@FeignClient(name = "ms-gestion-alumno", path = "/api/alumnos")
public interface AlumnoClient {

    @PostMapping
    AlumnoFichaDTO crear(@RequestBody AlumnoFichaDTO ficha);

    /** Accion compensatoria de la Saga: revierte la creacion de la ficha. */
    @DeleteMapping("/{id}")
    void eliminar(@PathVariable("id") Long id);
}
