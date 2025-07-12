package fr.inventory.packaging.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    /** Injecté par son mutateur */
   private JwtUtilities jwtUtilities ;


    ////////////////
    /// Méthodes ///
    ////////////////

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
                                    throws ServletException, IOException {
        if (request.getServletPath().equals("/api/rest/auth/login") || request.getServletPath().equals("/api/rest/auth/test")) {
            filterChain.doFilter(request, response);
            return;
        }
        String token = jwtUtilities.getToken(request);
        if (token != null && jwtUtilities.validateToken(token)) {
            String login = jwtUtilities.extractUsername(token);

            UserDetails userDetails = org.springframework.security.core.userdetails.User
                    .withUsername(login)
                    .password("")
                    .authorities(new ArrayList<>())
                    .build();

            if (jwtUtilities.validateToken(token, userDetails)) {
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        filterChain.doFilter(request,response);
    }

    /////////////////
    /// Mutateurs ///
    /////////////////

    @Autowired
    public void setJwtUtilities(JwtUtilities jwtUtilities) {
        this.jwtUtilities = jwtUtilities;
    }
}
