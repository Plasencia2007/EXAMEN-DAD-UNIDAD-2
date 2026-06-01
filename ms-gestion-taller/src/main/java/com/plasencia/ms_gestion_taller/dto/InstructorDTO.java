package com.plasencia.ms_gestion_taller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Representacion del Instructor tal como lo devuelve el microservicio
 * ms-gestion-instructor (consumido via OpenFeign).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InstructorDTO {

    private Long id;
    private String nombres;
    private String apellidos;
    private String especialidad;
    private String correo;
    private String telefono;

    // Marca de resiliencia: null/true = dato real; false = el servicio no respondio (fallback)
    private Boolean disponible;
}
