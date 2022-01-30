package com.cdprete.phonebook.webservice;

import com.cdprete.phonebook.exceptions.ClientHttpException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.validation.ConstraintViolationException;

import static org.slf4j.LoggerFactory.getLogger;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

/**
 * @author Cosimo Damiano Prete
 * @since 09/02/2022
 */
@ControllerAdvice
class WebServiceExceptionHandler {

    @ExceptionHandler(ClientHttpException.class)
    ResponseEntity<ErrorResponse> handleClientHttpException(ClientHttpException ex) {
        return new ResponseEntity<>(ErrorResponse.fromClientHttpException(ex), ex.statusCode());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        return handleInvalidInputException(ex.getParameter().getExecutable().getDeclaringClass(), ex);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(ConstraintViolationException ex) {
        return handleInvalidInputException(null, ex);
    }

    private static ResponseEntity<ErrorResponse> handleInvalidInputException(Class<?> caller, Exception ex) {
        logError(caller == null ? WebServiceExceptionHandler.class : caller, ex);

        var code = "invalid.input.data";
        var defaultMessage = "Some input data were invalid. Please check the logs for further details.";
        return new ResponseEntity<>(new ErrorResponse(code, defaultMessage), BAD_REQUEST);
    }

    private static <E extends Exception> void logError(Class<?> clazz, E ex) {
        getLogger(clazz).error(ex.getMessage(), ex);
    }

    private record ErrorResponse(String code, String defaultMessage) {
        private static <E extends ClientHttpException> ErrorResponse fromClientHttpException(E ex) {
            return new ErrorResponse(ex.clientCode(), ex.getMessage());
        }
    }
}
