package com.plasencia.ms_gestion_taller.gateway;

import com.plasencia.ms_gestion_taller.client.AlumnoClient;
import com.plasencia.ms_gestion_taller.dto.AlumnoDTO;
import feign.FeignException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * Envoltura resiliente del cliente Feign de alumnos.
 */
@Component
@RequiredArgsConstructor
public class AlumnoGateway {

    private final AlumnoClient alumnoClient;

    @CircuitBreaker(name = "alumno")
    @Retry(name = "alumno", fallbackMethod = "fallbackObtener")
    public AlumnoDTO obtenerAlumno(Long id) {
        return alumnoClient.obtenerAlumno(id);
    }

    private AlumnoDTO fallbackObtener(Long id, Throwable causa) {
        if (causa instanceof FeignException.NotFound) {
            return null;
        }
        return AlumnoDTO.builder()
                .id(id)
                .nombres("No disponible")
                .apellidos("")
                .disponible(false)
                .build();
    }

    @CircuitBreaker(name = "alumno")
    @Retry(name = "alumno", fallbackMethod = "fallbackPorIds")
    public List<AlumnoDTO> obtenerAlumnosPorIds(List<Long> ids) {
        return alumnoClient.obtenerAlumnosPorIds(ids);
    }

    /**
     * Fallback de la lista: si el servicio de alumnos no responde, la vista
     * compuesta del taller se muestra sin la lista de alumnos (degradacion).
     */
    private List<AlumnoDTO> fallbackPorIds(List<Long> ids, Throwable causa) {
        return Collections.emptyList();
    }
}
