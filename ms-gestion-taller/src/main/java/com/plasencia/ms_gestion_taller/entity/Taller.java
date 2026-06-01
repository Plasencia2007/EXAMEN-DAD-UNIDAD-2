package com.plasencia.ms_gestion_taller.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "taller")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Taller ofertado. El idInstructor referencia a ms-gestion-instructor.")
public class Taller {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador del taller", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @NotBlank(message = "El nombre del taller es obligatorio")
    @Column(nullable = false)
    @Schema(description = "Nombre del taller", example = "Soldadura basica", requiredMode = Schema.RequiredMode.REQUIRED)
    private String nombre;

    @Schema(description = "Descripcion del taller", example = "Introduccion a la soldadura MIG/MAG")
    private String descripcion;

    @Schema(description = "Fecha de inicio del taller", example = "2026-07-15")
    private LocalDate fechaInicio;

    @NotNull(message = "El cupo maximo es obligatorio")
    @Positive(message = "El cupo maximo debe ser mayor a cero")
    @Schema(description = "Cupo maximo de alumnos", example = "20", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer cupoMaximo;

    // Referencia al instructor (vive en el microservicio ms-gestion-instructor)
    @NotNull(message = "El id del instructor es obligatorio")
    @Column(nullable = false)
    @Schema(description = "Id del instructor que dicta el taller", example = "3", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long idInstructor;
}
