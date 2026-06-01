package com.plasencia.ms_gestion_taller.client;

import com.plasencia.ms_gestion_taller.dto.InstructorDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Cliente OpenFeign hacia el microservicio de instructores.
 * El "name" coincide con el registrado en Eureka, por lo que la llamada
 * se balancea automaticamente (no se usa la URL fisica).
 */
@FeignClient(name = "ms-gestion-instructor", path = "/api/instructores")
public interface InstructorClient {

    @GetMapping("/{id}")
    InstructorDTO obtenerInstructor(@PathVariable("id") Long id);
}
