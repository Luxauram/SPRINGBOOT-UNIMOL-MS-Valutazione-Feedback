package it.unimol.microservice_assessment_feedback.security;

import it.unimol.microservice_assessment_feedback.common.util.JWTValidationService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    @Autowired
    private JWTValidationService jwtValidationService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();
        if (path.startsWith("/health") ||
                path.startsWith("/actuator") ||
                path.startsWith("/swagger-ui") ||
                path.startsWith("/v3/api-docs") ||
                path.equals("/swagger-ui.html")) {
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");
        String userId = null;
        String userRole = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            try {
                String token = jwtValidationService.extractTokenFromHeader(authHeader);
                JWTValidationService.UserInfo userInfo = jwtValidationService.validateTokenAndGetUserInfo(token);
                userId = userInfo.userId();
                userRole = userInfo.role();

                logger.debug("JWT Estratto: User ID='{}', Ruolo='{}'", userId, userRole);

            } catch (Exception e) {
                logger.error("Errore durante la validazione del token JWT: {}", e.getMessage());
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("{\"error\":\"Token JWT non valido: " + e.getMessage() + "\"}");
                return;
            }
        }

        if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            String springSecurityRole = userRole;
            if (springSecurityRole != null && !springSecurityRole.startsWith("ROLE_")) {
                springSecurityRole = "ROLE_" + springSecurityRole;
            }

            List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority(springSecurityRole));
            logger.debug("Autorità concesse a Spring Security: {}", authorities);

            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(userId, null, authorities);

            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            logger.info("Utente '{}' autenticato con ruolo Spring Security '{}'", userId, springSecurityRole);
        } else if (authHeader != null && !authHeader.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"Formato del token non valido. Deve iniziare con 'Bearer '\"}");
            return;
        }

        filterChain.doFilter(request, response);
    }
}

