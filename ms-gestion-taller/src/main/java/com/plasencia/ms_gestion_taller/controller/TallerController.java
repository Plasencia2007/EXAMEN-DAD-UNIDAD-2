package com.plasencia.ms_gestion_taller.controller;

import com.plasencia.ms_gestion_taller.dto.TallerDetalleDTO;
import com.plasencia.ms_gestion_taller.entity.Inscripcion;
import com.plasencia.ms_gestion_taller.entity.Taller;
import com.plasencia.ms_gestion_taller.service.TallerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/talleres")
@RequiredArgsConstructor
@Tag(name = "Talleres", description = "CRUD de talleres, vista compuesta e inscripcion de alumnos")
public class TallerController {

    private final TallerService tallerService;

    // ---------- CRUD ----------

    @Operation(summary = "Listar todos los talleres",
            description = "Acceso: cualquier usuario autenticado.")
    @GetMapping
    public ResponseEntity<List<Taller>> listar() {
        return ResponseEntity.ok(tallerService.listar());
    }

    @Operation(summary = "Buscar un taller por su id",
            description = "Acceso: cualquier usuario autenticado.")
    @GetMapping("/{id}")
    public ResponseEntity<Taller> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(tallerService.buscarPorId(id));
    }

    @Operation(summary = "Crear un nuevo taller",
            description = "Acceso: INSTRUCTOR o ADMIN.")
    @PostMapping
    public ResponseEntity<Taller> crear(@Valid @RequestBody Taller taller) {
        return new ResponseEntity<>(tallerService.crear(taller), HttpStatus.CREATED);
    }

    @Operation(summary = "Actualizar un taller existente",
            description = "Acceso: INSTRUCTOR o ADMIN.")
    @PutMapping("/{id}")
    public ResponseEntity<Taller> actualizar(@PathVariable Long id,
                                             @Valid @RequestBody Taller taller) {
        return ResponseEntity.ok(tallerService.actualizar(id, taller));
    }

    @Operation(summary = "Eliminar un taller",
            description = "Acceso: INSTRUCTOR o ADMIN.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        tallerService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    // ---------- Vista compuesta y inscripciones ----------

    @Operation(summary = "Obtener el detalle compuesto de un taller",
            description = "Devuelve el taller con su instructor y la lista de alumnos inscritos "
                    + "(datos traidos via Feign de instructor y alumno).")
    @GetMapping("/{id}/detalle")
    public ResponseEntity<TallerDetalleDTO> obtenerDetalle(@PathVariable Long id) {
        return ResponseEntity.ok(tallerService.obtenerDetalle(id));
    }

    @Operation(summary = "Inscribir un alumno en un taller (proceso de negocio)",
            description = "Ejecuta las validaciones de negocio (cupo, duplicados, etc.). "
                    + "Acceso: ALUMNO o ADMIN.")
    @PostMapping("/{idTaller}/inscribir/{idAlumno}")
    public ResponseEntity<Inscripcion> inscribirAlumno(@PathVariable Long idTaller,
                                                       @PathVariable Long idAlumno) {
        Inscripcion inscripcion = tallerService.inscribirAlumno(idTaller, idAlumno);
        return new ResponseEntity<>(inscripcion, HttpStatus.CREATED);
    }

    @Operation(summary = "Cancelar la inscripcion de un alumno en un taller",
            description = "Acceso: ALUMNO o ADMIN.")
    @DeleteMapping("/{idTaller}/inscribir/{idAlumno}")
    public ResponseEntity<Void> cancelarInscripcion(@PathVariable Long idTaller,
                                                    @PathVariable Long idAlumno) {
        tallerService.cancelarInscripcion(idTaller, idAlumno);
        return ResponseEntity.noContent().build();
    }
}
