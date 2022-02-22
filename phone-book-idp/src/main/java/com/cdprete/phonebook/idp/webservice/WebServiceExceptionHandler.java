package com.cdprete.phonebook.idp.webservice;

import com.cdprete.phonebook.idp.exceptions.UsernameAlreadyRegisteredException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;

/**
 * @author Cosimo Damiano Prete
 * @since 20/02/2022
 */
@ControllerAdvice
class WebServiceExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(WebServiceExceptionHandler.class);

    @ExceptionHandler(UsernameAlreadyRegisteredException.class)
    ResponseEntity<ErrorResponse> handleUsernameAlreadyRegisteredException(UsernameAlreadyRegisteredException ex) {
        return new ResponseEntity<>(new ErrorResponse(ex.getClientCode(), ex.getMessage()), ex.statusCode());
    }

    @ExceptionHandler(BadCredentialsException.class)
    ResponseEntity<Void> handleBadCredentialsException(BadCredentialsException ex) {
        logger.error(ex.getMessage(), ex);

        return new ResponseEntity<>(UNAUTHORIZED);
    }

    private record ErrorResponse(String code, String defaultMessage) {}
}
