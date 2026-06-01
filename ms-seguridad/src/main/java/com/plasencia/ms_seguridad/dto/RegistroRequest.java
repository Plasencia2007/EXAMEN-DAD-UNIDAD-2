package com.plasencia.ms_seguridad.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Datos para registrar una nueva cuenta. El campo {@code rol} indica si el
 * usuario sera ALUMNO o INSTRUCTOR (se acepta con o sin el prefijo ROLE_).
 *
 * Ademas de la credencial, trae los datos de la FICHA de negocio que se creara
 * automaticamente en el microservicio correspondiente:
 *  - INSTRUCTOR: requiere {@code especialidad}.
 *  - ALUMNO: requiere {@code dni}.
 */
@Data
public class RegistroRequest {

    // ----- Credencial (tabla usuario) -----
    @NotBlank(message = "El nombre de usuario es obligatorio")
    private String username;

    @Email(message = "El correo debe tener un formato valido")
    @NotBlank(message = "El correo es obligatorio")
    private String correo;

    @NotBlank(message = "La contrasena es obligatoria")
    @Size(min = 6, message = "La contrasena debe tener al menos 6 caracteres")
    private String password;

    @NotBlank(message = "El rol es obligatorio (ALUMNO o INSTRUCTOR)")
    private String rol;

    // ----- Datos de la ficha (instructor / alumno) -----
    @NotBlank(message = "Los nombres son obligatorios")
    private String nombres;

    @NotBlank(message = "Los apellidos son obligatorios")
    private String apellidos;

    private String telefono;

    /** Solo para INSTRUCTOR. */
    private String especialidad;

    /** Solo para ALUMNO. */
    private String dni;
}
