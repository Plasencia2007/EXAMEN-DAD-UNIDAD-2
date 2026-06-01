package com.plasencia.ms_seguridad.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Cuerpo para crear la ficha de alumno en ms-gestion-alumno
 * (y para recibir de vuelta el id generado).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlumnoFichaDTO {

    private Long id;
    private String nombres;
    private String apellidos;
    private String dni;
    private String correo;
    private String telefono;
}
