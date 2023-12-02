package account.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class Custom400Exception extends RuntimeException {

    public Custom400Exception(String message) {
        super(message);
    }

    public Custom400Exception(String message, Throwable cause) {
        super(message, cause);
    }
}
