package com.plasencia.ms_gestion_taller.exception;

/**
 * Se lanza cuando un microservicio externo (instructor o alumno) no responde
 * y el circuito esta abierto. Se traduce a HTTP 503 (Service Unavailable).
 */
public class ServicioNoDisponibleException extends RuntimeException {

    public ServicioNoDisponibleException(String mensaje) {
        super(mensaje);
    }
}
