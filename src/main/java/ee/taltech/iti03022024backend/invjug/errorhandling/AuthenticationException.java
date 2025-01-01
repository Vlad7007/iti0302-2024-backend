package ee.taltech.iti03022024backend.invjug.errorhandling;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Getter
@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class AuthenticationException extends RuntimeException {
    private final String field;

    public AuthenticationException(String field, String message) {
        super(message);
        this.field = field;
    }

}