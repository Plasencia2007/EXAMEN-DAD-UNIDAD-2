package com.plasencia.ms_gestion_taller.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
public class Taller {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre del taller es obligatorio")
    @Column(nullable = false)
    private String nombre;

    private String descripcion;

    private LocalDate fechaInicio;

    @NotNull(message = "El cupo maximo es obligatorio")
    @Positive(message = "El cupo maximo debe ser mayor a cero")
    private Integer cupoMaximo;

    // Referencia al instructor (vive en el microservicio ms-gestion-instructor)
    @NotNull(message = "El id del instructor es obligatorio")
    @Column(nullable = false)
    private Long idInstructor;
}
