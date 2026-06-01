package com.plasencia.ms_seguridad.controller;

import com.plasencia.ms_seguridad.dto.AuthResponse;
import com.plasencia.ms_seguridad.dto.LoginRequest;
import com.plasencia.ms_seguridad.dto.RegistroRequest;
import com.plasencia.ms_seguridad.service.AuthService;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticacion", description = "Registro y login. Devuelve el JWT (endpoints publicos)")
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "Registrar un nuevo usuario",
            description = "Crea el usuario y devuelve su JWT. Endpoint publico.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Usuario creado; devuelve el JWT",
                    content = @Content(schema = @Schema(implementation = AuthResponse.class))),
            @ApiResponse(responseCode = "400", description = "Datos invalidos o usuario ya existente",
                    content = @Content)
    })
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> registrar(@Valid @RequestBody RegistroRequest request) {
        return new ResponseEntity<>(authService.registrar(request), HttpStatus.CREATED);
    }

    @Operation(summary = "Iniciar sesion",
            description = "Valida credenciales y devuelve el JWT a usar en los demas servicios. "
                    + "Endpoint publico.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Credenciales validas; devuelve el JWT",
                    content = @Content(schema = @Schema(implementation = AuthResponse.class))),
            @ApiResponse(responseCode = "400", description = "Body invalido (validacion)",
                    content = @Content),
            @ApiResponse(responseCode = "401", description = "Credenciales incorrectas",
                    content = @Content)
    })
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}
