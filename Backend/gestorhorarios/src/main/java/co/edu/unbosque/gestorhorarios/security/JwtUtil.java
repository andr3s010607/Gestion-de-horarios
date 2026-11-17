package co.edu.unbosque.gestorhorarios.security;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import co.edu.unbosque.gestorhorarios.entity.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {

    private static final long JWT_TOKEN_VALIDITY = 24 * 60 * 60 * 1000;
    private static final String CLAIM_VERSION_CONTRASENA = "ver";
    private static final int LONGITUD_HUELLA_BYTES = 8;

    @Value("${jwt.secret}")
    private String secret;

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        return claimsResolver.apply(extractAllClaims(token));
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        if (userDetails instanceof Usuario usuario) {
            claims.put("id", usuario.getId());
            claims.put("nombreUsuario", usuario.getNombreUsuario());
            claims.put("rol", usuario.getRol().name());
        }
        claims.put(CLAIM_VERSION_CONTRASENA, huellaContrasena(userDetails.getPassword()));
        return createToken(claims, userDetails.getUsername());
    }

    /**
     * Huella corta del hash de la contraseña. Al cambiar la contraseña cambia la
     * huella, y los tokens emitidos antes dejan de ser válidos.
     */
    private static String huellaContrasena(String hashContrasena) {
        try {
            byte[] resumen = MessageDigest.getInstance("SHA-256")
                    .digest(String.valueOf(hashContrasena).getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(resumen, 0, LONGITUD_HUELLA_BYTES);
        } catch (NoSuchAlgorithmException excepcion) {
            throw new IllegalStateException("El algoritmo SHA-256 no está disponible", excepcion);
        }
    }

    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        Claims claims = extractAllClaims(token);
        return claims.getSubject().equals(userDetails.getUsername())
                && !claims.getExpiration().before(new Date())
                && huellaContrasena(userDetails.getPassword()).equals(claims.get(CLAIM_VERSION_CONTRASENA));
    }
}
