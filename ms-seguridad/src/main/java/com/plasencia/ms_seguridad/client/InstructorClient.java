package com.plasencia.ms_seguridad.client;

import com.plasencia.ms_seguridad.dto.InstructorFichaDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Cliente OpenFeign hacia el microservicio de instructores. Se usa para crear
 * la ficha al registrar un INSTRUCTOR y para eliminarla (compensacion Saga).
 */
@FeignClient(name = "ms-gestion-instructor", path = "/api/instructores")
public interface InstructorClient {

    @PostMapping
    InstructorFichaDTO crear(@RequestBody InstructorFichaDTO ficha);

    /** Accion compensatoria de la Saga: revierte la creacion de la ficha. */
    @DeleteMapping("/{id}")
    void eliminar(@PathVariable("id") Long id);
}
