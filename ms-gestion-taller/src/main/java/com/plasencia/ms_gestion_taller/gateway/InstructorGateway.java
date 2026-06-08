package com.plasencia.ms_gestion_taller.gateway;

import com.plasencia.ms_gestion_taller.client.InstructorClient;
import com.plasencia.ms_gestion_taller.dto.InstructorDTO;
import feign.FeignException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InstructorGateway {

    private final InstructorClient instructorClient;

    @CircuitBreaker(name = "instructor")
    @Retry(name = "instructor", fallbackMethod = "fallbackObtener")
    public InstructorDTO obtenerInstructor(Long id) {
        return instructorClient.obtenerInstructor(id);
    }

    private InstructorDTO fallbackObtener(Long id, Throwable causa) {
        if (causa instanceof FeignException.NotFound) {
            return null;
        }
        return InstructorDTO.builder()
                .id(id)
                .nombres("No disponible")
                .apellidos("")
                .especialidad("Servicio de instructores no disponible")
                .disponible(false)
                .build();
    }
}
