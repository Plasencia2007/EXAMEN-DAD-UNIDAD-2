package com.plasencia.ms_gestion_taller.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

/**
 * Configuracion de OpenAPI / Swagger para el microservicio compuesto de talleres.
 * Define los datos generales de la API y el esquema de seguridad JWT (Bearer),
 * de modo que el boton "Authorize" de Swagger UI permita probar los endpoints
 * protegidos pegando el token obtenido en el login de ms-seguridad.
 */
@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "API - Microservicio de Talleres (servicio compuesto)",
                version = "1.0",
                description = "Gestiona talleres e inscripciones. Consulta instructor y alumno "
                        + "via OpenFeign con Resilience4j (Circuit Breaker + Retry).",
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
