package ee.taltech.iti03022024backend.invjug.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import ee.taltech.iti03022024backend.invjug.errorhandling.ErrorResponse;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtRequestFilter extends OncePerRequestFilter {

    private final SecretKey key;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        try {
            log.info("Request URI: {}", request.getRequestURI());

            Optional<String> token = getToken(request);

            token.ifPresentOrElse(
                    tokenValue -> {
                        log.info("Token found: {}", token.get());
                        Claims tokenBody = parseToken(token.get());
                        SecurityContext context = SecurityContextHolder.getContext();
                        context.setAuthentication(buildAuthToken(tokenBody));
                    },
                    () -> log.info("No token found")
            );

        } catch (SignatureException e) {
            log.error("Invalid JWT signature", e);

            ErrorResponse errorResponse = new ErrorResponse("Invalid JWT signature");
            ResponseEntity<Object> responseEntity = ResponseEntity.badRequest().body(errorResponse);
            response.setStatus(responseEntity.getStatusCode().value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().write(new ObjectMapper().writeValueAsString(responseEntity.getBody()));

            return;
        }
        chain.doFilter(request, response);
    }

    private Optional<String> getToken(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader("Authorization"))
                .filter(header -> header.startsWith("Bearer "))
                .map(header -> header.substring(7));
    }

    private Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private Authentication buildAuthToken(Claims tokenBody) {
        return new UsernamePasswordAuthenticationToken(
                tokenBody.getSubject(), null, List.of(new SimpleGrantedAuthority("USER"))
        );
    }
}
