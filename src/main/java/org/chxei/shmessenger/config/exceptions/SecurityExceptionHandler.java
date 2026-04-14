package org.chxei.shmessenger.config.exceptions;

import org.chxei.shmessenger.utils.response.CustomResponseEntity;
import org.chxei.shmessenger.utils.response.ResponseCode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class SecurityExceptionHandler {

    @ExceptionHandler(org.springframework.security.authentication.BadCredentialsException.class)
    public ResponseEntity<CustomResponseEntity> handleBadCredentialsException(org.springframework.security.authentication.BadCredentialsException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new CustomResponseEntity(ResponseCode.WRONG_USERNAME_PASSWORD));
    }

    @ExceptionHandler(org.springframework.security.authentication.DisabledException.class)
    public ResponseEntity<CustomResponseEntity> handleDisabledException(org.springframework.security.authentication.DisabledException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new CustomResponseEntity(ResponseCode.USER_INACTIVE));
    }

    @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
    public ResponseEntity<CustomResponseEntity> handleAccessDeniedException(org.springframework.security.access.AccessDeniedException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new CustomResponseEntity(ResponseCode.WRONG_JWT, e.getMessage()));
    }
}
