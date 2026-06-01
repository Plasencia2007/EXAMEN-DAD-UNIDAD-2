package com.plasencia.ms_gestion_taller.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Representa la inscripcion de un alumno en un taller.
 * Solo guarda los identificadores; los datos completos del alumno se obtienen
 * por OpenFeign desde el microservicio ms-gestion-alumno.
 */
@Entity
@Table(name = "inscripcion")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Inscripcion de un alumno en un taller (solo guarda los identificadores).")
public class Inscripcion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador de la inscripcion", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @NotNull(message = "El id del taller es obligatorio")
    @Column(nullable = false)
    @Schema(description = "Id del taller", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long idTaller;

    @NotNull(message = "El id del alumno es obligatorio")
    @Column(nullable = false)
    @Schema(description = "Id del alumno inscrito", example = "5", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long idAlumno;
}
