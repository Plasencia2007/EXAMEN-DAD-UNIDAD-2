package com.plasencia.ms_admin_api_gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

        @Bean
        public RouteLocator rutas(RouteLocatorBuilder builder) {
                return builder.routes()
                                // Microservicio de seguridad (login/register + usuarios/roles/permisos)
                                .route("ms-seguridad", r -> r
                                                .path("/api/auth/**", "/api/usuarios/**",
                                                                "/api/roles/**", "/api/permisos/**")
                                                .uri("lb://ms-seguridad"))
                                // Microservicio de instructores
                                .route("ms-gestion-instructor", r -> r
                                                .path("/api/instructores/**")
                                                .uri("lb://ms-gestion-instructor"))
                                // Microservicio de alumnos
                                .route("ms-gestion-alumno", r -> r
                                                .path("/api/alumnos/**")
                                                .uri("lb://ms-gestion-alumno"))
                                // Microservicio compuesto de talleres
                                .route("ms-gestion-taller", r -> r
                                                .path("/api/talleres/**", "/api/inscripciones/**")
                                                .uri("lb://ms-gestion-taller"))

                                // ----------------------------------------------------------
                                // Rutas de documentacion OpenAPI para el Swagger UI agregado.
                                // El agregador (springdoc.swagger-ui.urls) pide, de forma
                                // relativa, /<servicio>/v3/api-docs. Aqui reescribimos ese
                                // prefijo y reenviamos al /v3/api-docs real de cada servicio,
                                // de forma deterministica (sin depender del discovery locator).
                                // ----------------------------------------------------------
                                .route("ms-seguridad-docs", r -> r
                                                .path("/ms-seguridad/v3/api-docs")
                                                .filters(f -> f.rewritePath(
                                                                "/ms-seguridad/v3/api-docs", "/v3/api-docs"))
                                                .uri("lb://ms-seguridad"))
                                .route("ms-gestion-instructor-docs", r -> r
                                                .path("/ms-gestion-instructor/v3/api-docs")
                                                .filters(f -> f.rewritePath(
                                                                "/ms-gestion-instructor/v3/api-docs", "/v3/api-docs"))
                                                .uri("lb://ms-gestion-instructor"))
                                .route("ms-gestion-alumno-docs", r -> r
                                                .path("/ms-gestion-alumno/v3/api-docs")
                                                .filters(f -> f.rewritePath(
                                                                "/ms-gestion-alumno/v3/api-docs", "/v3/api-docs"))
                                                .uri("lb://ms-gestion-alumno"))
                                .route("ms-gestion-taller-docs", r -> r
                                                .path("/ms-gestion-taller/v3/api-docs")
                                                .filters(f -> f.rewritePath(
                                                                "/ms-gestion-taller/v3/api-docs", "/v3/api-docs"))
                                                .uri("lb://ms-gestion-taller"))
                                .build();
        }
}
