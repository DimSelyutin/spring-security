package by.plamya.project.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import java.util.Date;
import java.util.Map;
import java.util.HashMap;
import by.plamya.project.entity.User;
import by.plamya.project.exceptions.UserTokenExpiredException;
import by.plamya.project.utils.constants.SecurityConstants;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;

@Component
public class JWTTokenProvider {
    public static final Logger LOG = LoggerFactory.getLogger(JWTTokenProvider.class);

    // Метод для генерации JWT токена на основе аутентификации пользователя
    public String generateToken(User user) {

        // User user = (User) authentication.getPrincipal();
        Date now = new Date(System.currentTimeMillis());
        Date expiryDate = new Date(now.getTime() + SecurityConstants.EXPIRATION_TIME);

        Map<String, Object> claimsMap = buildClaims(user);

        return Jwts.builder()
                .setSubject(user.getEmail())
                .addClaims(claimsMap)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS512, SecurityConstants.SECRET)
                .compact();
    }

    // Метод для создания утверждений (claims) на основе данных пользователя
    private Map<String, Object> buildClaims(User user) {
        Map<String, Object> claimsMap = new HashMap<>();
        claimsMap.put("id", Long.toString(user.getId()));
        claimsMap.put("email", user.getEmail());
        // claimsMap.put("lastname", user.getLastname());
        claimsMap.put("username", user.getUsername());

        return claimsMap;
    }

    // Метод для проверки валидности JWT токена
    public boolean validateToken(String token) {

        try {
            Jwts.parser()
                    .setSigningKey(SecurityConstants.SECRET)
                    .parseClaimsJws(token);
            return true;
        } catch (SignatureException | MalformedJwtException | ExpiredJwtException | UnsupportedJwtException
                | IllegalArgumentException ex) {
            LOG.error("validateToken exception: {}", ex.getMessage());
            throw new UserTokenExpiredException("Token expired!");
        }
    }

    // Метод для извлечения идентификатора пользователя из JWT токена
    public Long getUserIdFromToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(SecurityConstants.SECRET)
                .parseClaimsJws(token)
                .getBody();
        String id = (String) claims.get("id");
        return Long.parseLong(id);
    }
}
