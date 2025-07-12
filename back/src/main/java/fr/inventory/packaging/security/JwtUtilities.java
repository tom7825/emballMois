package fr.inventory.packaging.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.function.Function;

@Component
public class JwtUtilities {

    /////////////////
    /// Attributs ///
    /////////////////

    private static final Logger logger = LogManager.getLogger();

    @Value("${jwt.secret}")
    private String secret;
    @Value("${jwt.expiration}")
    private Long jwtExpiration;

    ////////////////
    /// Méthodes ///
    ////////////////

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        final String email = extractUsername(token);
        return (email.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    public Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public String generateToken(String email , List<String> roles) {
        return Jwts.builder()
                .setSubject(email)
                .claim("role",roles)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(Date.from(Instant.now().plus(jwtExpiration, ChronoUnit.MILLIS)))
                .signWith(SignatureAlgorithm.HS256, secret).compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(secret).parseClaimsJws(token);
            return true;
        } catch (SignatureException e) {
            logger.error("Signature JWT invalide.", e);
        } catch (MalformedJwtException e) {
            logger.error("Token JWT invalide.", e);
        } catch (ExpiredJwtException e) {
            logger.error("Token JWT expiré.", e);
        } catch (UnsupportedJwtException e) {
            logger.error("Token JWT non supporté.", e);
        } catch (IllegalArgumentException e) {
            logger.error("Arguments du token JWT invalides.", e);
        }
        return false;
    }

    public String getToken (HttpServletRequest httpServletRequest) {
         final String bearerToken = httpServletRequest.getHeader("Authorization");
         if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
             return bearerToken.substring(7);
         } else {
             return null;
         }
    }
}
