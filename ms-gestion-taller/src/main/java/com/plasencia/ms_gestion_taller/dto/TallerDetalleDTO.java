package com.plasencia.ms_gestion_taller.dto;

import com.plasencia.ms_gestion_taller.entity.Taller;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Vista compuesta de un taller con su instructor y alumnos inscritos "
        + "(agregados via OpenFeign desde instructor y alumno).")
public class TallerDetalleDTO {

    @Schema(description = "Datos propios del taller")
    private Taller taller;

    @Schema(description = "Instructor del taller (traido de ms-gestion-instructor)")
    private InstructorDTO instructor;

    @Schema(description = "Alumnos inscritos (traidos de ms-gestion-alumno)")
    private List<AlumnoDTO> alumnosInscritos;

    @Schema(description = "Cupos aun disponibles (cupoMaximo - inscritos)", example = "12")
    private long cuposDisponibles;
}
