package com.plasencia.ms_gestion_taller.dto;

import com.plasencia.ms_gestion_taller.entity.Taller;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Vista COMPUESTA de un taller: combina los datos propios del taller con el
 * instructor y los alumnos inscritos, obtenidos de otros microservicios via Feign.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TallerDetalleDTO {

    private Taller taller;
    private InstructorDTO instructor;
    private List<AlumnoDTO> alumnosInscritos;
    private long cuposDisponibles;
}
