package com.plasencia.ms_gestion_taller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Instructor agregado al detalle del taller (origen: ms-gestion-instructor).")
public class InstructorDTO {

    @Schema(description = "Id del instructor", example = "3")
    private Long id;

    @Schema(description = "Nombres", example = "Maria")
    private String nombres;

    @Schema(description = "Apellidos", example = "Quispe")
    private String apellidos;

    @Schema(description = "Especialidad", example = "Soldadura")
    private String especialidad;

    @Schema(description = "Correo de contacto", example = "maria.quispe@example.com")
    private String correo;

    @Schema(description = "Telefono de contacto", example = "987654321")
    private String telefono;

    // Marca de resiliencia: null/true = dato real; false = el servicio no respondio (fallback)
    @Schema(description = "Marca de resiliencia: null/true = dato real; false = fallback (servicio caido)",
            example = "true")
    private Boolean disponible;
}
