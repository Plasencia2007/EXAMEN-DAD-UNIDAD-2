package com.plasencia.ms_gestion_instructor.controller;

import com.plasencia.ms_gestion_instructor.entity.Instructor;
import com.plasencia.ms_gestion_instructor.service.InstructorService;
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
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/instructores")
@RequiredArgsConstructor
@Tag(name = "Instructores", description = "CRUD de instructores")
// Respuestas de error transversales a todos los endpoints del controlador.
@ApiResponses({
        @ApiResponse(responseCode = "401", description = "No autenticado (falta o caduco el JWT)",
                content = @Content),
        @ApiResponse(responseCode = "403", description = "Autenticado pero sin rol suficiente",
                content = @Content)
})
public class InstructorController {

    private final InstructorService instructorService;

    @Operation(summary = "Listar todos los instructores",
            description = "Acceso: cualquier usuario autenticado.")
    @ApiResponse(responseCode = "200", description = "Lista de instructores",
            content = @Content(schema = @Schema(implementation = Instructor.class)))
    @GetMapping
    public ResponseEntity<List<Instructor>> listar() {
        return ResponseEntity.ok(instructorService.listar());
    }

    @Operation(summary = "Buscar un instructor por su id",
            description = "Acceso: cualquier usuario autenticado.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Instructor encontrado",
                    content = @Content(schema = @Schema(implementation = Instructor.class))),
            @ApiResponse(responseCode = "404", description = "No existe un instructor con ese id",
                    content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<Instructor> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(instructorService.buscarPorId(id));
    }

    @Operation(summary = "Crear un nuevo instructor",
            description = "Acceso: INSTRUCTOR o ADMIN.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Instructor creado",
                    content = @Content(schema = @Schema(implementation = Instructor.class))),
            @ApiResponse(responseCode = "400", description = "Datos invalidos (validacion del body)",
                    content = @Content)
    })
    @PostMapping
    public ResponseEntity<Instructor> crear(@Valid @RequestBody Instructor instructor) {
        return new ResponseEntity<>(instructorService.guardar(instructor), HttpStatus.CREATED);
    }

    @Operation(summary = "Actualizar un instructor existente",
            description = "Acceso: INSTRUCTOR o ADMIN.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Instructor actualizado",
                    content = @Content(schema = @Schema(implementation = Instructor.class))),
            @ApiResponse(responseCode = "400", description = "Datos invalidos (validacion del body)",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "No existe un instructor con ese id",
                    content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<Instructor> actualizar(@PathVariable Long id,
                                                 @Valid @RequestBody Instructor instructor) {
        return ResponseEntity.ok(instructorService.actualizar(id, instructor));
    }

    @Operation(summary = "Eliminar un instructor",
            description = "Acceso: INSTRUCTOR o ADMIN.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Instructor eliminado (sin contenido)",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "No existe un instructor con ese id",
                    content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        instructorService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
