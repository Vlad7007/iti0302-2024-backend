package ee.taltech.iti03022024backend.invjug.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import ee.taltech.iti03022024backend.invjug.errorhandling.ErrorResponse;
import ee.taltech.iti03022024backend.invjug.repository.InvalidTokenRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
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
    private final InvalidTokenRepository invalidTokenRepository;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain chain) throws IOException {
        try {
            log.info("Request URI: {}", request.getRequestURI());

            Optional<String> token = getToken(request);

            token.ifPresentOrElse(
                    tokenValue -> {
                        log.info("Token found: {}", tokenValue);
                        Claims tokenBody = parseToken(tokenValue);
                        SecurityContext context = SecurityContextHolder.getContext();
                        context.setAuthentication(buildAuthToken(tokenBody));
                    },
                    () -> log.info("No token found")
            );
            chain.doFilter(request, response);

        } catch (SignatureException e) {
            handleException(response, "Invalid JWT signature", HttpStatus.FORBIDDEN, e);
        } catch (ExpiredJwtException e) {
            handleException(response, "JWT token has expired", HttpStatus.FORBIDDEN, e);
        } catch (Exception e) {
            handleException(response, "An error occurred while processing the JWT", HttpStatus.INTERNAL_SERVER_ERROR, e);
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return request.getRequestURI().startsWith("/api/auth/register") ||
                request.getRequestURI().startsWith("/api/auth/login");
    }

    private Optional<String> getToken(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader("Authorization"))
                .filter(header -> header.startsWith("Bearer "))
                .map(header -> header.substring(7));
    }

    private Claims parseToken(String token) {
        if (invalidTokenRepository.existsByToken(token)) {
            throw new SignatureException("Token is invalidated");
        }
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private Authentication buildAuthToken(Claims tokenBody) {
        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(tokenBody.get("role", String.class)));
        return new UsernamePasswordAuthenticationToken(
                tokenBody.getSubject(), null, authorities
        );
    }

    private void handleException(HttpServletResponse response, String message, HttpStatus status, Exception e) throws IOException {
        log.error(message, e);
        ErrorResponse errorResponse = new ErrorResponse(message);
        ResponseEntity<Object> responseEntity = ResponseEntity.status(status).body(errorResponse);
        response.setStatus(responseEntity.getStatusCode().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(new ObjectMapper().writeValueAsString(responseEntity.getBody()));
    }
}
