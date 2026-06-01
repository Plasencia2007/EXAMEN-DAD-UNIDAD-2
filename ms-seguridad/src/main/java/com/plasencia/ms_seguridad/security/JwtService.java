package com.plasencia.ms_seguridad.security;

import com.plasencia.ms_seguridad.entity.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

@Service
public class JwtService {

    private final SecretKey clave;
    private final long expiracionMs;

    public JwtService(@Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration}") long expiracionMs) {
        this.clave = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expiracionMs = expiracionMs;
    }

    public String generarToken(Usuario usuario) {
        List<String> roles = usuario.getRoles().stream()
                .map(rol -> rol.getNombre())
                .collect(Collectors.toList());

        Set<String> permisos = new TreeSet<>();
        usuario.getRoles().forEach(rol -> rol.getPermisos().forEach(p -> permisos.add(p.getNombre())));

        Date ahora = new Date();
        Date expira = new Date(ahora.getTime() + expiracionMs);

        return Jwts.builder()
                .subject(usuario.getUsername())
                .claim("uid", usuario.getId())
                .claim("roles", roles)
                .claim("permisos", List.copyOf(permisos))
                .issuedAt(ahora)
                .expiration(expira)
                .signWith(clave)
                .compact();
    }

    public long getExpiracionSegundos() {
        return expiracionMs / 1000;
    }

    /**
     * Token de SERVICIO (corta vida) con rol ADMIN, usado por ms-seguridad para
     * crear la ficha del usuario en instructor/alumno durante el registro, cuando
     * el usuario todavia no tiene token propio.
     */
    public String generarTokenServicio() {
        Date ahora = new Date();
        Date expira = new Date(ahora.getTime() + 60_000); // 1 minuto
        return Jwts.builder()
                .subject("ms-seguridad")
                .claim("roles", List.of("ROLE_ADMIN"))
                .claim("permisos", List.of())
                .issuedAt(ahora)
                .expiration(expira)
                .signWith(clave)
                .compact();
    }

    public String extraerUsername(String token) {
        return parse(token).getSubject();
    }

    @SuppressWarnings("unchecked")
    public List<String> extraerAutoridades(String token) {
        Claims claims = parse(token);
        List<String> roles = claims.get("roles", List.class);
        List<String> permisos = claims.get("permisos", List.class);
        List<String> autoridades = new java.util.ArrayList<>();
        if (roles != null)
            autoridades.addAll(roles);
        if (permisos != null)
            autoridades.addAll(permisos);
        return autoridades;
    }

    public boolean esValido(String token) {
        try {
            parse(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private Claims parse(String token) {
        return Jwts.parser()
                .verifyWith(clave)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
