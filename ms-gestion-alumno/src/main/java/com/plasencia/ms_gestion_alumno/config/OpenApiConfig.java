package com.plasencia.ms_gestion_alumno.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

/**
 * Configuracion de OpenAPI / Swagger para el microservicio de alumnos.
 * Define los datos generales de la API y el esquema de seguridad JWT (Bearer).
 */
@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "API - Microservicio de Alumnos",
                version = "1.0",
                description = "CRUD de alumnos. Lecturas para cualquier autenticado; "
                        + "escrituras solo INSTRUCTOR o ADMIN.",
                contact = @Contact(name = "Plasencia - Examen DAD Unidad 2")
        ),
        security = @SecurityRequirement(name = "bearerAuth")
)
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        description = "Pega el JWT obtenido en POST /api/auth/login (sin escribir la palabra Bearer)."
)
public class OpenApiConfig {
}
