package com.plasencia.ms_gestion_taller.controller;

import com.plasencia.ms_gestion_taller.entity.Inscripcion;
import com.plasencia.ms_gestion_taller.service.InscripcionService;
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

/**
 * CRUD explicito de las inscripciones (tabla {@code inscripcion}).
 */
@RestController
@RequestMapping("/api/inscripciones")
@RequiredArgsConstructor
@Tag(name = "Inscripciones", description = "CRUD directo de inscripciones (acceso: INSTRUCTOR o ADMIN)")
public class InscripcionController {

    private final InscripcionService inscripcionService;

    @Operation(summary = "Listar inscripciones",
            description = "Opcionalmente filtradas por taller con el parametro idTaller.")
    @GetMapping
    public ResponseEntity<List<Inscripcion>> listar(
            @RequestParam(value = "idTaller", required = false) Long idTaller) {
        List<Inscripcion> resultado = (idTaller != null)
                ? inscripcionService.listarPorTaller(idTaller)
                : inscripcionService.listar();
        return ResponseEntity.ok(resultado);
    }

    @Operation(summary = "Buscar una inscripcion por su id")
    @GetMapping("/{id}")
    public ResponseEntity<Inscripcion> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(inscripcionService.buscarPorId(id));
    }

    @Operation(summary = "Crear una inscripcion directamente (operacion CRUD, sin reglas de negocio)")
    @PostMapping
    public ResponseEntity<Inscripcion> crear(@Valid @RequestBody Inscripcion inscripcion) {
        return new ResponseEntity<>(inscripcionService.crear(inscripcion), HttpStatus.CREATED);
    }

    @Operation(summary = "Actualizar una inscripcion existente")
    @PutMapping("/{id}")
    public ResponseEntity<Inscripcion> actualizar(@PathVariable Long id,
                                                  @Valid @RequestBody Inscripcion inscripcion) {
        return ResponseEntity.ok(inscripcionService.actualizar(id, inscripcion));
    }

    @Operation(summary = "Eliminar una inscripcion")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        inscripcionService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
