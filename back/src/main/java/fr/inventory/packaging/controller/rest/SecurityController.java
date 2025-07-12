package fr.inventory.packaging.controller.rest;

import fr.inventory.packaging.security.AuthRequest;
import fr.inventory.packaging.security.AuthResponse;
import fr.inventory.packaging.security.JwtUtilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/rest/auth")
@CrossOrigin("${front.url}")
public class SecurityController {

    /////////////////
    /// Attributs ///
    /////////////////

    @Value("${security.password}")
    private String configuredPassword;

    private JwtUtilities jwtUtilities;

    /////////////////
    /// Endpoints ///
    /////////////////

    @PostMapping("/login")
    public ResponseEntity<?> authenticate(@RequestBody AuthRequest request) {
        if (!configuredPassword.equals(request.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Mot de passe incorrect");
        }
        String token = jwtUtilities.generateToken("admin", List.of());
        return ResponseEntity.ok(new AuthResponse(token));
    }

    ///////////////
    /// Setters ///
    ///////////////

    @Autowired
    public void setJwtUtilities(JwtUtilities jwtUtilities) {
        this.jwtUtilities = jwtUtilities;
    }
}

