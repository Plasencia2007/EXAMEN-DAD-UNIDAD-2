package com.plasencia.ms_seguridad.controller;

import com.plasencia.ms_seguridad.entity.Permiso;
import com.plasencia.ms_seguridad.service.PermisoService;
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
@RequestMapping("/api/permisos")
@RequiredArgsConstructor
@Tag(name = "Permisos", description = "Gestion de permisos (solo ADMIN)")
// Respuestas de error transversales a todos los endpoints del controlador.
@ApiResponses({
        @ApiResponse(responseCode = "401", description = "No autenticado (falta o caduco el JWT)",
                content = @Content),
        @ApiResponse(responseCode = "403", description = "Autenticado pero sin rol ADMIN",
                content = @Content)
})
public class PermisoController {

    private final PermisoService permisoService;

    @Operation(summary = "Listar todos los permisos")
    @ApiResponse(responseCode = "200", description = "Lista de permisos",
            content = @Content(schema = @Schema(implementation = Permiso.class)))
    @GetMapping
    public ResponseEntity<List<Permiso>> listar() {
        return ResponseEntity.ok(permisoService.listar());
    }

    @Operation(summary = "Buscar un permiso por su id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Permiso encontrado",
                    content = @Content(schema = @Schema(implementation = Permiso.class))),
            @ApiResponse(responseCode = "404", description = "No existe un permiso con ese id",
                    content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<Permiso> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(permisoService.buscarPorId(id));
    }

    @Operation(summary = "Crear un nuevo permiso")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Permiso creado",
                    content = @Content(schema = @Schema(implementation = Permiso.class))),
            @ApiResponse(responseCode = "400", description = "Datos invalidos (validacion del body)",
                    content = @Content)
    })
    @PostMapping
    public ResponseEntity<Permiso> crear(@Valid @RequestBody Permiso permiso) {
        return new ResponseEntity<>(permisoService.guardar(permiso), HttpStatus.CREATED);
    }

    @Operation(summary = "Actualizar un permiso existente")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Permiso actualizado",
                    content = @Content(schema = @Schema(implementation = Permiso.class))),
            @ApiResponse(responseCode = "400", description = "Datos invalidos (validacion del body)",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "No existe un permiso con ese id",
                    content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<Permiso> actualizar(@PathVariable Long id,
                                              @Valid @RequestBody Permiso permiso) {
        return ResponseEntity.ok(permisoService.actualizar(id, permiso));
    }

    @Operation(summary = "Eliminar un permiso")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Permiso eliminado (sin contenido)",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "No existe un permiso con ese id",
                    content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        permisoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
