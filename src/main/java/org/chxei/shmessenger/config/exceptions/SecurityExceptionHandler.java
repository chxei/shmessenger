package org.chxei.shmessenger.config.exceptions;

import org.chxei.shmessenger.utils.response.CustomResponseEntity;
import org.chxei.shmessenger.utils.response.ResponseCode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class SecurityExceptionHandler {

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<CustomResponseEntity> handleBadCredentialsException() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new CustomResponseEntity(ResponseCode.WRONG_USERNAME_PASSWORD));
    }

    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<CustomResponseEntity> handleDisabledException() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new CustomResponseEntity(ResponseCode.USER_INACTIVE));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<CustomResponseEntity> handleAccessDeniedException(AccessDeniedException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new CustomResponseEntity(ResponseCode.WRONG_JWT, e.getMessage()));
    }
}
