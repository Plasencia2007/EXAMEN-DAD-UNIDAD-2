package com.plasencia.ms_seguridad.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

/**
 * Configuracion de OpenAPI / Swagger para el microservicio de seguridad.
 * Aqui viven login/register (publicos) y la gestion de usuarios/roles/permisos
 * (solo ADMIN). El esquema JWT permite probar los endpoints protegidos.
 */
@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "API - Microservicio de Seguridad",
                version = "1.0",
                description = "Login/registro y emision de JWT. Gestion de usuarios, roles y "
                        + "permisos (solo ADMIN).",
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
