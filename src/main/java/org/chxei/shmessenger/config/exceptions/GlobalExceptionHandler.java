package org.chxei.shmessenger.config.exceptions;

import org.chxei.shmessenger.utils.response.CustomResponseEntity;
import org.chxei.shmessenger.utils.response.ResponseCode;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<CustomResponseEntity> handleDataIntegrityViolationException(DataIntegrityViolationException e) {
        if (e.getMostSpecificCause() instanceof SQLException sqlException) {
            if (sqlException.getErrorCode() == 19 || sqlException.getErrorCode() == 2067) {
                String message = sqlException.getMessage();
                if (message != null) {
                    if (message.contains("username")) {
                        return ResponseEntity.status(HttpStatus.CONFLICT).body(new CustomResponseEntity(ResponseCode.CONSTRAINT_UNIQUE_USERS_USERNAME_VIOLATION));
                    } else if (message.contains("email")) {
                        return ResponseEntity.status(HttpStatus.CONFLICT).body(new CustomResponseEntity(ResponseCode.CONSTRAINT_UNIQUE_USERS_EMAIL_VIOLATION));
                    } else if (message.contains("phone")) {
                        return ResponseEntity.status(HttpStatus.CONFLICT).body(new CustomResponseEntity(ResponseCode.CONSTRAINT_UNIQUE_USERS_PHONE_VIOLATION));
                    }
                }
                return ResponseEntity.status(HttpStatus.CONFLICT).body(new CustomResponseEntity(ResponseCode.USER_UNIQUE_CONSTRAINT_VIOLATION, message != null ? message : "Constraint violation"));
            }
        }
        
        // Fallback for general data integrity violations
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new CustomResponseEntity(ResponseCode.USER_UNKNOWN_CONSTRAINT_ERROR));
    }

    @ExceptionHandler(org.springframework.security.authentication.BadCredentialsException.class)
    public ResponseEntity<CustomResponseEntity> handleBadCredentialsException(org.springframework.security.authentication.BadCredentialsException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new CustomResponseEntity(ResponseCode.WRONG_USERNAME_PASSWORD));
    }

    @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
    public ResponseEntity<CustomResponseEntity> handleAccessDeniedException(org.springframework.security.access.AccessDeniedException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new CustomResponseEntity(ResponseCode.WRONG_JWT, e.getMessage()));
    }
}
