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
                                .route("ms-seguridad", r -> r
                                                .path("/api/auth/**", "/api/usuarios/**",
                                                                "/api/roles/**", "/api/permisos/**")
                                                .uri("lb://ms-seguridad"))
                                .route("ms-gestion-instructor", r -> r
                                                .path("/api/instructores/**")
                                                .uri("lb://ms-gestion-instructor"))
                                .route("ms-gestion-alumno", r -> r
                                                .path("/api/alumnos/**")
                                                .uri("lb://ms-gestion-alumno"))
                                .route("ms-gestion-taller", r -> r
                                                .path("/api/talleres/**", "/api/inscripciones/**")
                                                .uri("lb://ms-gestion-taller"))

                               
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
