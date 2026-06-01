package com.plasencia.ms_gestion_alumno.controller;

import com.plasencia.ms_gestion_alumno.entity.Alumno;
import com.plasencia.ms_gestion_alumno.service.AlumnoService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/alumnos")
@RequiredArgsConstructor
@Tag(name = "Alumnos", description = "CRUD de alumnos")
public class AlumnoController {

    private final AlumnoService alumnoService;

    @Operation(summary = "Listar todos los alumnos",
            description = "Acceso: cualquier usuario autenticado.")
    @GetMapping
    public ResponseEntity<List<Alumno>> listar() {
        return ResponseEntity.ok(alumnoService.listar());
    }

    @Operation(summary = "Buscar un alumno por su id",
            description = "Acceso: cualquier usuario autenticado.")
    @GetMapping("/{id}")
    public ResponseEntity<Alumno> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(alumnoService.buscarPorId(id));
    }

    @Operation(summary = "Buscar varios alumnos por una lista de ids",
            description = "Endpoint usado por el microservicio de talleres para traer varios "
                    + "alumnos a la vez (vista compuesta).")
    @GetMapping("/por-ids")
    public ResponseEntity<List<Alumno>> buscarPorIds(@RequestParam List<Long> ids) {
        return ResponseEntity.ok(alumnoService.buscarPorIds(ids));
    }

    @Operation(summary = "Crear un nuevo alumno",
            description = "Acceso: INSTRUCTOR o ADMIN.")
    @PostMapping
    public ResponseEntity<Alumno> crear(@Valid @RequestBody Alumno alumno) {
        return new ResponseEntity<>(alumnoService.guardar(alumno), HttpStatus.CREATED);
    }

    @Operation(summary = "Actualizar un alumno existente",
            description = "Acceso: INSTRUCTOR o ADMIN.")
    @PutMapping("/{id}")
    public ResponseEntity<Alumno> actualizar(@PathVariable Long id,
                                             @Valid @RequestBody Alumno alumno) {
        return ResponseEntity.ok(alumnoService.actualizar(id, alumno));
    }

    @Operation(summary = "Eliminar un alumno",
            description = "Acceso: INSTRUCTOR o ADMIN.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        alumnoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
