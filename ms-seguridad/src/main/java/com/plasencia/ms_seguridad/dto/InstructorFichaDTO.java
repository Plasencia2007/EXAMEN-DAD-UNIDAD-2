package com.plasencia.ms_seguridad.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Cuerpo para crear la ficha de instructor en ms-gestion-instructor
 * (y para recibir de vuelta el id generado).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InstructorFichaDTO {

    private Long id;
    private String nombres;
    private String apellidos;
    private String especialidad;
    private String correo;
    private String telefono;
}
