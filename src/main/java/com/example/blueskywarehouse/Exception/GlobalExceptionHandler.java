package com.example.blueskywarehouse.Exception;

import com.example.blueskywarehouse.Response.ApiResponse;
import jakarta.persistence.OptimisticLockException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.naming.AuthenticationException;
import java.nio.file.AccessDeniedException;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // Geschäftsbezogene Ausnahmen behandeln
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<?>> handleBusinessException(BusinessException ex) {
        logger.warn("Geschäftsbezogene Ausnahmen: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND) // 404
                .body(ApiResponse.failure(HttpStatus.NOT_FOUND.value(), ex.getMessage()));
    }

    // Behandelt VersionConflictException (Optimistischer Sperrkonflikt, HTTP 409).
    @ExceptionHandler(VersionConflictException.class)
    public ResponseEntity<ApiResponse<?>> handleVersionConflictException(VersionConflictException ex) {
        logger.info("Version conflict detected: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.CONFLICT) // 409
                .body(ApiResponse.failure(HttpStatus.CONFLICT.value(), ex.getMessage()));
    }

    // Authentication Fehler
    @ExceptionHandler(org.springframework.security.core.AuthenticationException.class)
    public ResponseEntity<?> auth(AuthenticationException e) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED) // 401
                .body(Map.of(
                        "code", HttpStatus.UNAUTHORIZED.value(),
                        "message", "Unauthenticated"
                ));
    }

    // Access Denied Fehler
    @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
    public ResponseEntity<?> denied(AccessDeniedException e) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN) // 403
                .body(Map.of(
                        "code", HttpStatus.FORBIDDEN.value(),
                        "message", "Access denied， Sie haben keine Berechtigung."
                ));
    }

    // Ungültige Eingabeparameter
    @ExceptionHandler(InvalidParameterException.class)
    public ResponseEntity<ApiResponse<?>> handleInvalidParameter(InvalidParameterException ex) {
        return ResponseEntity
                .badRequest() // 400
                .body(ApiResponse.failure(HttpStatus.BAD_REQUEST.value(), ex.getMessage()));
    }

    // Behandlung von Datenbankausnahmen (JPA, JDBC-bezogen)
    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ApiResponse<?>> handleDatabaseException(DataAccessException ex) {
        logger.error("Datenbankfehler: {}", ex.getMessage(), ex);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR) // 500
                .body(ApiResponse.failure(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        "Datenbankfehler. Bitte wenden Sie sich an den Administrator."));
    }

    // Behandlung von Aktualisierungskonflikten bei Optimistic Locking
    @ExceptionHandler(OptimisticLockException.class)
    public ResponseEntity<ApiResponse<?>> handleOptimisticLockException(OptimisticLockException ex) {
        logger.warn("Optimistic-Locking-Konflikt: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.CONFLICT) // 409
                .body(ApiResponse.failure(HttpStatus.CONFLICT.value(),
                        "Die Daten wurden geändert. Bitte aktualisieren Sie die Seite und versuchen Sie es erneut."));
    }

    // Fallback-Ausnahme auf Systemebene
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleOtherException(Exception ex) {
        logger.error("Fallback-Ausnahme:", ex);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR) // 500
                .body(ApiResponse.failure(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        "Systemfehler. Bitte versuchen Sie es später erneut."));
    }
}
