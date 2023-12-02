package account.exception;

import account.DTO.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public ResponseEntity<Object> handleValidationException(MethodArgumentNotValidException ex) {

        List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();
        List<String> validationErrors = new ArrayList<>();

        for (FieldError fieldError : fieldErrors) {

            if (fieldError.getField().equals("password")) {
                return new ResponseEntity<>(new PasswordTooShortResponse(), HttpStatus.BAD_REQUEST);
            }
        }

        return new ResponseEntity<>(new UserExistResponse(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(PaymentException.class)
    public ResponseEntity<PaymentFailedResponse> handlePaymentException(PaymentException ex) {
        return new ResponseEntity<>(new PaymentFailedResponse(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(GetPaymentException.class)
    public ResponseEntity<GetPaymentFailedResponse> handleGetPaymentException(GetPaymentException ex) {
        return new ResponseEntity<>(new GetPaymentFailedResponse(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UserNotExistException.class)
    public ResponseEntity<UserNotExistResponse> handleUserNotExistException(UserNotExistException ex) {
        return new ResponseEntity<>(new UserNotExistResponse(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AdministratorNotRemoveException.class)
    public ResponseEntity<AdministratorNotRemoveResponse> handleAdministratorNotRemoveException(AdministratorNotRemoveException ex,
        HttpServletRequest request) {

        String path = request.getRequestURI();

        return new ResponseEntity<>(new AdministratorNotRemoveResponse(path), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EmailNotFoundException.class)
    public ResponseEntity<Response> handleEmailNotFoundException(EmailNotFoundException ex,
                                                                 HttpServletRequest request) {


        Response response = new Response();
        response.setStatus(404);
        response.setError("Not Found");
        response.setPath(request.getRequestURI());
        response.setMessage("User not found!");

        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Response> handleNotFoundException(NotFoundException ex,
                                                                 HttpServletRequest request) {


        Response response = new Response();
        response.setStatus(404);
        response.setError("Not Found");
        response.setPath(request.getRequestURI());
        response.setMessage("Role not found!");

        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Custom404Exception.class)
    public ResponseEntity<Response> handeCustom404Exception(Custom404Exception ex,
                                                            HttpServletRequest request) {
        Response response = new Response();
        response.setStatus(404);
        response.setError("Not Found");
        response.setPath(request.getRequestURI());
        response.setMessage(ex.getMessage());

        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Custom400Exception.class)
    public ResponseEntity<Response> handeCustom400Exception(Custom400Exception ex,
                                                            HttpServletRequest request) {
        Response response = new Response();
        response.setStatus(400);
        response.setError("Bad Request");
        response.setPath(request.getRequestURI());
        response.setMessage(ex.getMessage());

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}
