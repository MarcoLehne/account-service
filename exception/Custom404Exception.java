package account.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class Custom404Exception extends RuntimeException {

    public Custom404Exception(String message) {
        super(message);
    }

    public Custom404Exception(String message, Throwable cause) {
        super(message, cause);
    }
}
