package com.plasencia.ms_gestion_taller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Representacion del Alumno tal como lo devuelve el microservicio
 * ms-gestion-alumno (consumido via OpenFeign).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Alumno agregado al detalle del taller (origen: ms-gestion-alumno).")
public class AlumnoDTO {

    @Schema(description = "Id del alumno", example = "5")
    private Long id;

    @Schema(description = "Nombres", example = "Juan")
    private String nombres;

    @Schema(description = "Apellidos", example = "Perez")
    private String apellidos;

    @Schema(description = "DNI", example = "70123456")
    private String dni;

    @Schema(description = "Correo de contacto", example = "juan.perez@example.com")
    private String correo;

    @Schema(description = "Telefono de contacto", example = "987123456")
    private String telefono;

    // Marca de resiliencia: null/true = dato real; false = el servicio no respondio (fallback)
    @Schema(description = "Marca de resiliencia: null/true = dato real; false = fallback (servicio caido)",
            example = "true")
    private Boolean disponible;
}
