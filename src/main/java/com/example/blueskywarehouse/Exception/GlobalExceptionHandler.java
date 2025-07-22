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

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // Geschäftsbezogene Ausnahmen behandeln
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<?>> handleBusinessException(BusinessException ex) {
        logger.warn("Geschäftsbezogene Ausnahmen: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.failure(404, ex.getMessage()));
    }
    // Ungültige Eingabeparameter
    @ExceptionHandler(InvalidParameterException.class)
    public ResponseEntity<ApiResponse<?>> handleInvalidParameter(InvalidParameterException ex) {
        return ResponseEntity
                .badRequest()
                .body(ApiResponse.failure(400, ex.getMessage()));
    }

    // Behandlung von Datenbankausnahmen (JPA, JDBC-bezogen)
    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ApiResponse<?>> handleDatabaseException(DataAccessException ex) {
        logger.error("Datenbankfehler: {}", ex.getMessage(), ex);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.failure(500, "Datenbankfehler. Bitte wenden Sie sich an den Administrator."));
    }
    // Behandlung von Aktualisierungskonflikten bei Optimistic Locking
    @ExceptionHandler(OptimisticLockException.class)
    public ResponseEntity<ApiResponse<?>> handleOptimisticLockException(OptimisticLockException ex) {
        logger.warn("Optimistic-Locking-Konflikt: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.CONFLICT) // 409冲突
                .body(ApiResponse.failure(409, "Die Daten wurden geändert. Bitte aktualisieren Sie die Seite und versuchen Sie es erneut."));
    }

    //Fallback-Ausnahme auf Systemebene
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleOtherException(Exception ex) {
        logger.error("Fallback-Ausnahme:", ex);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.failure(500, "Systemfehler. Bitte versuchen Sie es später erneut."));
    }
}
