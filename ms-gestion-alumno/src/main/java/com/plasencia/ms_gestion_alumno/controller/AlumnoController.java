package com.plasencia.ms_gestion_alumno.controller;

import com.plasencia.ms_gestion_alumno.entity.Alumno;
import com.plasencia.ms_gestion_alumno.service.AlumnoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
// Respuestas de error transversales a todos los endpoints del controlador.
@ApiResponses({
        @ApiResponse(responseCode = "401", description = "No autenticado (falta o caduco el JWT)",
                content = @Content),
        @ApiResponse(responseCode = "403", description = "Autenticado pero sin rol suficiente",
                content = @Content)
})
public class AlumnoController {

    private final AlumnoService alumnoService;

    @Operation(summary = "Listar todos los alumnos",
            description = "Acceso: cualquier usuario autenticado.")
    @ApiResponse(responseCode = "200", description = "Lista de alumnos",
            content = @Content(schema = @Schema(implementation = Alumno.class)))
    @GetMapping
    public ResponseEntity<List<Alumno>> listar() {
        return ResponseEntity.ok(alumnoService.listar());
    }

    @Operation(summary = "Buscar un alumno por su id",
            description = "Acceso: cualquier usuario autenticado.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Alumno encontrado",
                    content = @Content(schema = @Schema(implementation = Alumno.class))),
            @ApiResponse(responseCode = "404", description = "No existe un alumno con ese id",
                    content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<Alumno> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(alumnoService.buscarPorId(id));
    }

    @Operation(summary = "Buscar varios alumnos por una lista de ids",
            description = "Endpoint usado por el microservicio de talleres para traer varios "
                    + "alumnos a la vez (vista compuesta).")
    @ApiResponse(responseCode = "200", description = "Alumnos encontrados",
            content = @Content(schema = @Schema(implementation = Alumno.class)))
    @GetMapping("/por-ids")
    public ResponseEntity<List<Alumno>> buscarPorIds(@RequestParam List<Long> ids) {
        return ResponseEntity.ok(alumnoService.buscarPorIds(ids));
    }

    @Operation(summary = "Crear un nuevo alumno",
            description = "Acceso: INSTRUCTOR o ADMIN.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Alumno creado",
                    content = @Content(schema = @Schema(implementation = Alumno.class))),
            @ApiResponse(responseCode = "400", description = "Datos invalidos (validacion del body)",
                    content = @Content)
    })
    @PostMapping
    public ResponseEntity<Alumno> crear(@Valid @RequestBody Alumno alumno) {
        return new ResponseEntity<>(alumnoService.guardar(alumno), HttpStatus.CREATED);
    }

    @Operation(summary = "Actualizar un alumno existente",
            description = "Acceso: INSTRUCTOR o ADMIN.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Alumno actualizado",
                    content = @Content(schema = @Schema(implementation = Alumno.class))),
            @ApiResponse(responseCode = "400", description = "Datos invalidos (validacion del body)",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "No existe un alumno con ese id",
                    content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<Alumno> actualizar(@PathVariable Long id,
                                             @Valid @RequestBody Alumno alumno) {
        return ResponseEntity.ok(alumnoService.actualizar(id, alumno));
    }

    @Operation(summary = "Eliminar un alumno",
            description = "Acceso: INSTRUCTOR o ADMIN.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Alumno eliminado (sin contenido)",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "No existe un alumno con ese id",
                    content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        alumnoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
