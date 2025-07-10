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

    // 处理业务异常
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<?>> handleBusinessException(BusinessException ex) {
        logger.warn("业务异常: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.failure(404, ex.getMessage()));
    }
    // 输入参数异常
    @ExceptionHandler(InvalidParameterException.class)
    public ResponseEntity<ApiResponse<?>> handleInvalidParameter(InvalidParameterException ex) {
        return ResponseEntity
                .badRequest()
                .body(ApiResponse.failure(400, ex.getMessage()));
    }

    // 处理数据库异常（JPA、JDBC 相关）
    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ApiResponse<?>> handleDatabaseException(DataAccessException ex) {
        logger.error("数据库异常: {}", ex.getMessage(), ex);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.failure(500, "数据库错误，请联系管理员"));
    }
    // 乐观锁出现更新冲突处理
    @ExceptionHandler(OptimisticLockException.class)
    public ResponseEntity<ApiResponse<?>> handleOptimisticLockException(OptimisticLockException ex) {
        logger.warn("乐观锁冲突: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.CONFLICT) // 409冲突
                .body(ApiResponse.failure(409, "数据已被修改，请刷新后重试"));
    }

    // 兜底异常（系统级）
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleOtherException(Exception ex) {
        logger.error("系统异常:", ex);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.failure(500, "系统异常，请稍后再试"));
    }
}
